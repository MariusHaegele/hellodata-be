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
package ch.bedag.dap.hellodata.commons.sidecars.resources.v1;

import ch.bedag.dap.hellodata.commons.sidecars.modules.ModuleResourceKind;
import ch.bedag.dap.hellodata.commons.sidecars.modules.ModuleType;
import ch.bedag.dap.hellodata.commons.sidecars.resources.v1.appinfo.AppInfoResource;
import ch.bedag.dap.hellodata.commons.sidecars.resources.v1.dashboard.DashboardResource;
import ch.bedag.dap.hellodata.commons.sidecars.resources.v1.permission.PermissionResource;
import ch.bedag.dap.hellodata.commons.sidecars.resources.v1.pipeline.PipelineResource;
import ch.bedag.dap.hellodata.commons.sidecars.resources.v1.role.RoleResource;
import ch.bedag.dap.hellodata.commons.sidecars.resources.v1.user.UserResource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({ @JsonSubTypes.Type(value = AppInfoResource.class, name = ModuleResourceKind.HELLO_DATA_APP_INFO),
        @JsonSubTypes.Type(value = DashboardResource.class, name = ModuleResourceKind.HELLO_DATA_DASHBOARDS),
        @JsonSubTypes.Type(value = UserResource.class, name = ModuleResourceKind.HELLO_DATA_USERS),
        @JsonSubTypes.Type(value = PermissionResource.class, name = ModuleResourceKind.HELLO_DATA_PERMISSIONS),
        @JsonSubTypes.Type(value = RoleResource.class, name = ModuleResourceKind.HELLO_DATA_ROLES),
        @JsonSubTypes.Type(value = PipelineResource.class, name = ModuleResourceKind.HELLO_DATA_PIPELINES)})
public interface HdResource extends Serializable {

    String NAME_FORMAT = "[api-version: %s][namespace: %s][module-type: %s][kind: %s][instance-name: %s]";
    String HD_MODULE_KEY = "hellodata/module";
    String URL_KEY = "url";

    String getApiVersion();

    ModuleType getModuleType();

    String getKind();

    String getInstanceName();

    Metadata getMetadata();

    Object getData();

    @JsonIgnore
    default String getSummary() {
        return String.format(NAME_FORMAT, getApiVersion(), getMetadata().namespace(), getModuleType(), getKind(), getInstanceName());
    }
}
