///
/// Copyright © 2024, Kanton Bern
/// All rights reserved.
///
/// Redistribution and use in source and binary forms, with or without
/// modification, are permitted provided that the following conditions are met:
///     * Redistributions of source code must retain the above copyright
///       notice, this list of conditions and the following disclaimer.
///     * Redistributions in binary form must reproduce the above copyright
///       notice, this list of conditions and the following disclaimer in the
///       documentation and/or other materials provided with the distribution.
///     * Neither the name of the <organization> nor the
///       names of its contributors may be used to endorse or promote products
///       derived from this software without specific prior written permission.
///
/// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
/// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
/// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
/// DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
/// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
/// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
/// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
/// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
/// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
/// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
///

import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ExternalDashboard, ExternalDashboardMetadata} from "./external-dashboards.model";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ExternalDashboardsService {
  externalDashboardsBaseUrl = `${environment.portalApi}/external-dashboards`;

  constructor(protected httpClient: HttpClient) {
  }

  public getExternalDashboards(): Observable<ExternalDashboard[]> {
    return this.httpClient.get<ExternalDashboard[]>(`${this.externalDashboardsBaseUrl}`);
  }

  public updateExternalDashboard(dashboard: ExternalDashboard): Observable<void> {
    return this.httpClient.put<void>(`${this.externalDashboardsBaseUrl}`, dashboard);
  }

  public createExternalDashboard(dashboard: ExternalDashboardMetadata): Observable<void> {
    return this.httpClient.post<void>(`${this.externalDashboardsBaseUrl}`, dashboard);
  }

  public deleteExternalDashboard(dashboard: ExternalDashboard): Observable<void> {
    return this.httpClient.delete<void>(`${this.externalDashboardsBaseUrl}/${dashboard.id}`);
  }

  getExternalDashboardById(dashboardId: string): Observable<ExternalDashboard> {
    return this.httpClient.get<ExternalDashboard>(`${this.externalDashboardsBaseUrl}/${dashboardId}`);
  }
}
