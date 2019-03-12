/*
 * Copyright 2018 ChemAxon Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.chemaxon.clustering.web.services;

import com.chemaxon.clustering.common.IDBasedClusterBuilder;
import com.chemaxon.clustering.web.dao.GroupingDao;
import com.chemaxon.clustering.web.entities.Grouping;
import com.chemaxon.clustering.web.entities.Molfile;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides domain specific operations for Grouping instances.
 *
 * @author Gabor Imre
 */
@Service
public class GroupingService {

    @Autowired
    private GroupingDao groupingDao;

    @Autowired
    private MolfilesService molfilesService;


    /**
     * Invoke a random grouping of structures into approximately equal sized clusters.
     *
     * @param molfile Source
     * @param clusterCount Maximal number of clusters to sort structures
     * @param idSuggestion Suggestion for ID of the result
     * @return Executed clustering
     */
    public Grouping invokeRandomClustering(Molfile molfile, int clusterCount, String idSuggestion ) {
        final long timeStart = System.currentTimeMillis();

        if (clusterCount > molfile.size()) {
            clusterCount = molfile.size();
        }

        // Do a full Fisher-Yates shuffle on initial molecule indices
        // Pseudocode of the shuffle (see wiki article above)
        //   for i from 0 to n-1 do
        //     a[i] = i
        //
        //   -- To shuffle an array a of n elements (indices 0..n-1):
        //   for i from 0 to n−2 do
        //     let j be a random integer such that i ≤ j < n
        //     exchange a[i] and a[j]

        final int n = molfile.size();
        final int [] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }

        for (int i = 0; i < n - 1; i++) {
            final int j = i + (int) Math.round((n - i - 1) * Math.random());

            final int ai = a[i];
            final int aj = a[j];

            a[i] = aj;
            a[j] = ai;
        }

        // add the first required elements
        final IDBasedClusterBuilder b = new IDBasedClusterBuilder();

        final int [] clusterIndices = new int[clusterCount];
        for (int i = 0; i < clusterCount; i++) {
            clusterIndices[i] = b.addNewCluster();
        }
        for (int i = 0; i < n; i++) {
            final int ci = clusterIndices[i % clusterCount];

            b.addStructureToCluster(
                a[i], // structure ID
                ci  // cluster ID
            );

            if (i < clusterCount) {
                b.updateRepresentant(a[i], ci);
            }
        }

        final long timeStop = System.currentTimeMillis();

        final Grouping g = new Grouping(
            b.build(),
            timeStop - timeStart,
            "Random clustering into " + clusterCount + " clusters from " + molfile.size()
        );

        this.groupingDao.add(idSuggestion, g);

        return g;


    }


    /**
     * Invoke a random selection.
     *
     * The result of a random selection is a single level clustering where the one cluster contains the randomly
     * selected molecule indices. The cluster representant is arbitrarily selected.
     *
     * @param molfile Source
     * @param selectedCount Maximal number of structures to select.
     * @param idSuggestion Suggestion for ID of the result
     * @return Executed clustering
     */
    public Grouping invokeRandomSelection(Molfile molfile, int selectedCount, String idSuggestion ) {

        final long timeStart = System.currentTimeMillis();

        if (selectedCount > molfile.size()) {
            selectedCount = molfile.size();
        }


        // Do a partial Fisher-Yates shuffle on the initial molecule indices
        // See https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
        // Pseudocode of the shuffle (see wiki article above)
        //   for i from 0 to n-1 do
        //     a[i] = i
        //
        //   -- To shuffle an array a of n elements (indices 0..n-1):
        //   for i from 0 to n−2 do
        //     let j be a random integer such that i ≤ j < n
        //     exchange a[i] and a[j]
        // As an optimization we store only the elements where a[i] != i

        final Map<Integer, Integer> a = new HashMap<>();
        final int n = molfile.size();
        final int endIndex = Math.min(n - 2, selectedCount - 1);
        for (int i = 0; i <= endIndex; i++) {
            final int j = i + (int) Math.round((n - i - 1) * Math.random());

            final int ai = a.getOrDefault(i, i);
            final int aj = a.getOrDefault(j, j);

            a.put(i, aj);
            a.put(j, ai);
        }

        // add the first required elements
        final IDBasedClusterBuilder b = new IDBasedClusterBuilder();
        final int ci = b.addNewCluster();
        for (int i = 0; i < selectedCount; i++) {
            b.addStructureToCluster(
                a.getOrDefault(i, i), // structure ID
                ci  // cluster ID
            );
        }
        // select an arbitrary cluster representant
        b.updateRepresentant(
            a.getOrDefault(0,0),
            ci
        );

        final long timeStop = System.currentTimeMillis();

        final Grouping g = new Grouping(
            b.build(),
            timeStop - timeStart,
            "Random selection of " + selectedCount + " elements from " + molfile.size()
        );

        this.groupingDao.add(idSuggestion, g);

        return g;
    }



    /**
     * Retrieve grouping by ID.
     *
     * @param id ID of {@link Grouping} to retrieve
     * @return Instance identified by the given ID.
     * @throws NoSuchElementException when given ID not found.
     */
    public Grouping getGrouping(String id) {
        return this.groupingDao.get(id);
    }


    /**
     * Retrieve all grouping instances.
     *
     * @return ID to grouping mapping.
     */
    public Map<String, Grouping>  getAllGroupings() {
        return this.groupingDao.getAll();
    }



    /**
     * Retrieve Grouping ID.
     *
     * @param grouping Instance
     * @return ID of instance
     * @throws NoSuchElementException when given ID not found for the specified instance
     */
    public String getGroupingId(Grouping grouping) {
        return this.groupingDao.getIdOf(grouping);
    }

    /**
     * Delete an existing grouping.
     *
     * @param grouping Instance
     */
    public void deleteGrouping(Grouping grouping) {
        this.groupingDao.delete(grouping);
    }



}
