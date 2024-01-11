/*
 * Copyright © 2024, Kanton Bern
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.bedag.dap.hellodata.commons.sidecars.resources.v1.pipeline;

import ch.bedag.dap.hellodata.commons.sidecars.modules.ModuleResourceKind;
import ch.bedag.dap.hellodata.commons.sidecars.modules.ModuleType;
import ch.bedag.dap.hellodata.commons.sidecars.resources.v1.HdResource;
import ch.bedag.dap.hellodata.commons.sidecars.resources.v1.Metadata;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class PipelineResource implements HdResource {
    @EqualsAndHashCode.Include
    private final String apiVersion = "v1";
    @EqualsAndHashCode.Include
    private final ModuleType moduleType = ModuleType.AIRFLOW;
    @EqualsAndHashCode.Include
    private final String kind = ModuleResourceKind.HELLO_DATA_PIPELINES;
    @EqualsAndHashCode.Include
    private String instanceName;
    private Metadata metadata;
    private List<Pipeline> data;

    /**
     * @param instanceName instance name
     * @param namespace    namespace
     * @param data         Dag Run information
     */
    public PipelineResource(String instanceName, String namespace, List<Pipeline> data) {
        this.instanceName = instanceName;
        Map<String, Object> labels = new HashMap<>();
        labels.put(HD_MODULE_KEY, moduleType.getModuleName());
        this.metadata = new Metadata(instanceName, namespace, labels);

        this.data = new ArrayList<>(data);
    }

    @Override
    public String toString() {
        return getSummary();
    }
}
