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

import {Component, OnInit, ViewChild} from '@angular/core';
import {Store} from "@ngrx/store";
import {AppState} from "../../store/app/app.state";
import {Observable} from "rxjs";
import {SupersetDashboard} from "../../store/my-dashboards/my-dashboards.model";
import {SupersetDashboardWithMetadata} from "../../store/start-page/start-page.model";
import {MenuService} from "../../store/menu/menu.service";
import {Table} from "primeng/table";
import {naviElements} from "../../app-navi-elements";
import {selectMyDashboards} from "../../store/my-dashboards/my-dashboards.selector";
import {BaseComponent} from "../../shared/components/base/base.component";
import {navigate} from "../../store/app/app.action";
import {createBreadcrumbs} from "../../store/breadcrumb/breadcrumb.action";
import {updateDashboardMetadata} from "../../store/start-page/start-page.action";
import {loadMyDashboards} from "../../store/my-dashboards/my-dashboards.action";

@Component({
  templateUrl: 'my-dashboards.component.html',
  styleUrls: ['./my-dashboards.component.scss']
})
export class MyDashboardsComponent extends BaseComponent implements OnInit {

  @ViewChild('dt') dt!: Table | undefined;

  dashboards$: Observable<SupersetDashboard[]>;
  editDashboardMetadataDialog = false;
  viewDashboardDataDialog = false;
  selectedDashboard!: SupersetDashboardWithMetadata;

  constructor(private store: Store<AppState>, private menuService: MenuService) {
    super();
    this.dashboards$ = this.store.select(selectMyDashboards);
    this.store.dispatch(createBreadcrumbs({
      breadcrumbs: [
        {
          label: naviElements.myDashboards.label,
          routerLink: naviElements.myDashboards.path
        }
      ]
    }));
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.store.dispatch(loadMyDashboards());
  }

  hideEditMetadataDialog() {
    this.editDashboardMetadataDialog = false;
  }

  updateDashboard(dashboard: SupersetDashboardWithMetadata) {
    this.selectedDashboard = {...dashboard};
    this.store.dispatch(updateDashboardMetadata({dashboard: this.selectedDashboard}))
    this.hideEditMetadataDialog();
  }

  createLink(dashboard: SupersetDashboardWithMetadata): string {
    return this.menuService.createDashboardLink(dashboard);
  }

  createImageLink(dashboard: SupersetDashboardWithMetadata): string {
    return dashboard.instanceUrl + dashboard.thumbnailPath;
  }

  editDashboard(dashboard: SupersetDashboardWithMetadata) {
    this.selectedDashboard = {...dashboard};
    this.editDashboardMetadataDialog = true;
  }

  onRowSelect($event: any) {
    const dashboardLink = this.menuService.createDashboardLink($event.data);
    this.store.dispatch(navigate({url: dashboardLink}));
  }

  applyFilterGlobal($event: any, stringVal: string) {
    if (this.dt) {
      this.dt.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
    }
  }

  openDashboard(dashboard: SupersetDashboardWithMetadata) {
    const dashboardLink = this.menuService.createDashboardLink(dashboard);
    this.store.dispatch(navigate({url: dashboardLink}));
  }

  openInfoPanel(dashboard: any) {
    this.selectedDashboard = {...dashboard};
    this.viewDashboardDataDialog = true;
  }

  hideInfoPanel() {
    this.viewDashboardDataDialog = false;
  }
}
