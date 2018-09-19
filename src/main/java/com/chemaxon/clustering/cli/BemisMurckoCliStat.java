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
package com.chemaxon.clustering.cli;

import com.chemaxon.calculations.common.TimerStatistics;
import com.chemaxon.overlap.cli.prof.Memstat;
import com.chemaxon.overlap.cli.prof.ProfSnapshot;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Execution statistics for {@link BemisMurckoCli}.
 *
 * @author Gabor Imre
 */
@XmlRootElement
public class BemisMurckoCliStat {

    /**
     * Name of tool.
     */
    public String tool = "BemisMurckoCli";


    /**
     * Number of target structures.
     *
     * Typically the number of input structures processed/parsed/clustered.
     */
    public int targetCount = 0;


    /**
     * Timer statistics for importing phase.
     */
    public TimerStatistics timestatImport = null;

    /**
     * Timer statistics for clustering phase when applicable.
     */
    public TimerStatistics timestatClustering = null;

    /**
     * Timer statistics for exploring phase when applicable.
     */
    public TimerStatistics timestatExport = null;


    // Fields managed by CLI invocation utilities ----------------------------------------------------------------------
    /**
     * Environment from {@link System#getenv()}.
     */
    public Map<String, String> env = null;

    /**
     * System properties from {@link System#getProperties()}.
     */
    public Map<String, String> props = null;

    /**
     * CLI parameters parsed.
     */
    public BemisMurckoCliParameters cliParameters = null;

    /**
     * CLI arguments.
     */
    public String [] cliArguments = null;

    /**
     * Start time from {@link System#currentTimeMillis()}.
     */
    public long startTime = 0;

    /**
     * Stop time from {@link System#currentTimeMillis()}.
     */
    public long stopTime = 0;

    /**
     * Elapsed time in ms.
     */
    public long elapsedTime = 0;

    /**
     * Final memory statistics.
     */
    public Memstat memstatFinal = null;

    /**
     * Initial memory statistics.
     */
    public Memstat memstatInitial = null;

    /**
     * More detailed VM state snapshot at the begining of execution.
     */
    public ProfSnapshot profSnapshotInitial = null;

    /**
     * More detailed VM state snapshot at the end of execution.
     */
    public ProfSnapshot profSnapshotFinal = null;


}


