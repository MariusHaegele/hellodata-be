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
package ch.bedag.dap.hellodata.portal.external_dashboard.controller;

import ch.bedag.dap.hellodata.portal.external_dashboard.data.CreateExternalDashboardDto;
import ch.bedag.dap.hellodata.portal.external_dashboard.data.ExternalDashboardDto;
import ch.bedag.dap.hellodata.portal.external_dashboard.data.UpdateExternalDashboardDto;
import ch.bedag.dap.hellodata.portal.external_dashboard.service.ExternalDashboardService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/external-dashboards")
public class ExternalDashboardController {
    private final ExternalDashboardService externalDashboardService;

    @GetMapping
    public List<ExternalDashboardDto> fetchExternalDashboards() {
        return externalDashboardService.getExternalDashboards();
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('EXTERNAL_DASHBOARDS_MANAGEMENT')")
    public void createExternalDashboard(@RequestBody @Valid CreateExternalDashboardDto createExternalDashboardDto) {
        externalDashboardService.create(createExternalDashboardDto);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('EXTERNAL_DASHBOARDS_MANAGEMENT')")
    public void updateExternalDashboard(@RequestBody @Valid UpdateExternalDashboardDto updateExternalDashboardDto) {
        externalDashboardService.update(updateExternalDashboardDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EXTERNAL_DASHBOARDS_MANAGEMENT')")
    public void deleteExternalDashboard(@PathVariable UUID id) {
        externalDashboardService.delete(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EXTERNAL_DASHBOARDS_MANAGEMENT')")
    public ExternalDashboardDto getExternalDashboard(@PathVariable UUID id) {
        return externalDashboardService.getExternalDashboard(id);
    }
}
