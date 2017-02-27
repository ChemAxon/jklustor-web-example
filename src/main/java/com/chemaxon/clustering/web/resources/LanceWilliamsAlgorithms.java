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
package com.chemaxon.clustering.web.resources;

import com.chemaxon.clustering.wards.LanceWilliamsMerge;
import com.chemaxon.clustering.wards.LanceWilliamsMerges;

/**
 * Applicable clustering algorithms.
 *
 * Declared constants are directly used by the REST API resource implementation as query parameters.
 *
 * @author Gabor Imre
 */
public enum LanceWilliamsAlgorithms {
    wards {
        @Override
        public LanceWilliamsMerge getMerge() {
            return new LanceWilliamsMerges.Wards();
        }
    }, singlelinkage {
        @Override
        public LanceWilliamsMerge getMerge() {
            return new LanceWilliamsMerges.SingleLinkage();
        }
    }, completelinkage {
        @Override
        public LanceWilliamsMerge getMerge() {
            return new LanceWilliamsMerges.CompleteLinkage();
        }
    }, averagelinkage {
        @Override
        public LanceWilliamsMerge getMerge() {
            return new LanceWilliamsMerges.AverageLinkage();
        }
    };

    public abstract LanceWilliamsMerge getMerge();

}
