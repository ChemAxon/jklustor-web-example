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
package com.chemaxon.clustering.web.application;

import com.chemaxon.clustering.web.services.MolfilesService;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Initialize default data.
 *
 * @author Gabor Imre
 */
// see https://springframework.guru/running-code-on-spring-boot-startup/
// see http://stackoverflow.com/questions/7484594/spring-3-0-inject-files-as-resources
@Component
public class DefaultDataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private MolfilesService molfilesService;

    @Value("classpath:/vitamins.smi")
    private Resource vitamins;

    @Value("classpath:/antibiotics.smi")
    private Resource antibiotics;

    @Value("classpath:/who-essential-medicines.smi")
    private Resource whoEssentialMedicines;

    private void addMolfileFromResource(Resource resource, String originalFileName) {
        try {
            try (InputStream is = resource.getInputStream()) {
                final byte [] data = IOUtils.toByteArray(is);
                this.molfilesService.addMolfile(originalFileName, data);
            }
        }  catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        addMolfileFromResource(vitamins, "vitamins.smi");
        addMolfileFromResource(antibiotics, "antibiotics.smi");
        addMolfileFromResource(whoEssentialMedicines, "who-essential-medicines.smi");
    }

}
