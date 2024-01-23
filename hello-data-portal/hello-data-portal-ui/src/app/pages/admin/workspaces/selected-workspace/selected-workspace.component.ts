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

import {Component, OnInit} from '@angular/core';
import {Observable, tap} from "rxjs";
import {Store} from "@ngrx/store";
import {AppState} from "../../../../store/app/app.state";
import {LoadAppInfoResources, LoadSelectedAppInfoResource, LoadSelectedAppInfoResources} from "../../../../store/metainfo-resource/metainfo-resource.action";
import {
  selectedAppInfoResource,
  selectedAppInfoResources,
  selectSelectedAppInfo,
  selectSelectedAppInfoResourcesParams
} from "../../../../store/metainfo-resource/metainfo-resource.selector";
import {Navigate} from "../../../../store/app/app.action";
import {BaseComponent} from "../../../../shared/components/base/base.component";

@Component({
  selector: 'app-selected-workspace',
  templateUrl: './selected-workspace.component.html',
  styleUrls: ['./selected-workspace.component.scss']
})
export class SelectedWorkspaceComponent extends BaseComponent implements OnInit {

  resources$: Observable<any>;
  selectedResource$: Observable<any>;
  headerInfo$: Observable<any>;
  selectedResourceUrl: any;
  selectedAppInfo$: Observable<any>;

  constructor(private store: Store<AppState>) {
    super();
    this.store.dispatch(new LoadAppInfoResources());
    this.store.dispatch(new LoadSelectedAppInfoResources());
    this.headerInfo$ = this.store.select(selectSelectedAppInfoResourcesParams);
    this.resources$ = this.store.select(selectedAppInfoResources);
    this.selectedResource$ = this.store.select(selectedAppInfoResource);
    this.selectedAppInfo$ = this.store.select(selectSelectedAppInfo).pipe(tap((appInfo: any) => {
      this.selectedResourceUrl = appInfo?.data?.url;
    }));
  }

  override ngOnInit(): void {
    super.ngOnInit();
  }

  navigateToSelectedResource(selectedResource: any) {
    this.store.dispatch(new LoadSelectedAppInfoResource(selectedResource));
  }

  cancel() {
    this.store.dispatch(new Navigate('workspaces'));
  }

}
