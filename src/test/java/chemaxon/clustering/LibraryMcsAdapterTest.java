/*
 * Copyright 2017 ChemAxon Ltd.
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
package chemaxon.clustering;

import static chemaxon.clustering.LibraryMcsAdapterTestutils.printoutHierarchy;
import chemaxon.clustering.adapter.LegacyLibraryMcsAdapter;
import chemaxon.clustering.adapter.LegacyLibraryMcsParameters;
import chemaxon.struc.Molecule;
import com.chemaxon.clustering.framework.FrameworkClusteringResults;
import com.chemaxon.testutils.inject.FromResource;
import com.chemaxon.testutils.inject.InjectorRunner;
import com.chemaxon.testutils.log.OnOffLogRule;
import static com.chemaxon.testutils.matchers.CanonicalSmilesMolMatcher.hasSameCanonicalSmiles;
import static com.chemaxon.testutils.molecule.MoleculeIo.moleculesOf;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

/**
 * Exercise {@link LegacyLibraryMcsAdapter}.
 *
 * @author Gabor Imre
 */
// InjectorRunner is responsible for handling the @FromResource annotation below
@RunWith(InjectorRunner.class)
public class LibraryMcsAdapterTest {

    /**
     * JUnit {@code TestRule} to augment test execution.
     *
     * This {@code TestRule} which is annotated with {@link Rule} will execute failing tests with a {@link Log}
     * setting where every log message is printed to the standard error.
     */
    @Rule
    public TestRule logRule = new OnOffLogRule(OnOffLogRule.VisibleLogRunPolicy.ONFAIL);

    /**
     * Logger to use.
     *
     * Please note that a custom {@link Log} defined by {@code disco-test-utils} package is used.
     */
    private final Log log = LogFactory.getLog(LibraryMcsAdapterTest.class);

    /**
     * Vitamins dataset.
     *
     * Contents of the resource {@code chemaxon/clustering/vitamins.smi} will be read line by line into this
     * {@code List} by the {@link InjectorRunner} used to execute this test suite.
     */
    @FromResource(resource = "vitamins.smi")
    private List<String> vitamins;

    @Test
    public void ensure_aromatization() {
        final LegacyLibraryMcsParameters params = LegacyLibraryMcsAdapter
                .defaultParameters()
                .setMinimumMCSSize(3);
        final List<String> src = ImmutableList.of("CC1=CC=CC=C1", "c1ccccc1");

        final FrameworkClusteringResults results = LegacyLibraryMcsAdapter.cluster(
                params,
                moleculesOf(src).iterator()
        );

        if (log.isDebugEnabled()) {
            printoutHierarchy(log, results, src);
        }

        assertThat(results.getFrameworksCount(), is(1));
        assertThat(results.getFrameworkAsMolecule(0), hasSameCanonicalSmiles("c1ccccc1"));
        assertThat(results.getHierarchy().roots().size(), is(1));
        assertThat(results.getHierarchy().maxHeight(), is(1));
    }

    @Test
    public void run_on_vitamins() {
        final LegacyLibraryMcsParameters params = LegacyLibraryMcsAdapter.defaultParameters();
        final FrameworkClusteringResults results = LegacyLibraryMcsAdapter.cluster(
                params,
                moleculesOf(vitamins).iterator()
        );

        if (log.isDebugEnabled()) {
            printoutHierarchy(log, results, vitamins);
        }
        assertThat(results.getHierarchy().maxHeight(), is(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void adapter_dies_on_empty() {
        // Empty dataset can not be clustered.
        final LegacyLibraryMcsParameters params = LegacyLibraryMcsAdapter.defaultParameters();
        LegacyLibraryMcsAdapter.cluster(params, ImmutableSet.<Molecule>of().iterator());
    }


    @Test
    public void adapter_on_single_molecule() {
        final LegacyLibraryMcsParameters params = LegacyLibraryMcsAdapter.defaultParameters();
        final List<String> src = ImmutableList.of("C");
        final FrameworkClusteringResults results = LegacyLibraryMcsAdapter.cluster(
                params,
                moleculesOf(src).iterator()
        );

        if (log.isDebugEnabled()) {
            printoutHierarchy(log, results, src);
        }

        assertThat(results.getFrameworksCount(), is(1));
        assertThat(results.getFrameworkAsSmiles(0), is("C"));
        assertThat(results.getHierarchy().roots().size(), is(1));
        assertThat(results.getHierarchy().maxHeight(), is(1));
    }

    @Test
    public void adapter_on_non_clustered_molecule() {
        final LegacyLibraryMcsParameters params = LegacyLibraryMcsAdapter.defaultParameters();
        final List<String> src = ImmutableList.of("C", "N", "O", "P");
        final FrameworkClusteringResults results = LegacyLibraryMcsAdapter.cluster(
                params,
                moleculesOf(src).iterator()
        );

        if (log.isDebugEnabled()) {
            printoutHierarchy(log, results, src);
        }

        assertThat(results.getFrameworksCount(), is(4));
        assertThat(results.getFrameworkAsSmiles(0), is("C"));
        assertThat(results.getFrameworkAsSmiles(1), is("N"));
        assertThat(results.getFrameworkAsSmiles(2), is("O"));
        assertThat(results.getFrameworkAsSmiles(3), is("P"));
        assertThat(results.getHierarchy().roots().size(), is(4));
        assertThat(results.getHierarchy().maxHeight(), is(1));
    }
}
