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

import {ComponentFixture, TestBed} from '@angular/core/testing';
import {LineageDocsComponent} from './lineage-docs.component';
import {Store} from '@ngrx/store';
import {LineageDoc} from '../../store/lineage-docs/lineage-docs.model';
import {naviElements} from '../../app-navi-elements';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {of} from 'rxjs';
import {AppState} from '../../store/app/app.state';
import {beforeEach, describe, expect, it, jest} from '@jest/globals';
import {LineageDocsService} from "../../store/lineage-docs/lineage-docs.service";
import {ButtonModule} from "primeng/button";
import {RippleModule} from "primeng/ripple";
import {TranslocoTestingModule} from "@ngneat/transloco";
import {DropdownModule} from "primeng/dropdown";
import {TooltipModule} from "primeng/tooltip";
import {SubsystemIframeModule} from "../../shared/components/subsystem-iframe/subsystem-iframe.component";
import {HdCommonModule} from "../../hd-common.module";
import {TableModule} from "primeng/table";
import {CreateBreadcrumbs} from "../../store/breadcrumb/breadcrumb.action";
import {navigate} from "../../store/app/app.action";

describe('LineageDocsComponent', () => {
  let component: LineageDocsComponent;
  let fixture: ComponentFixture<LineageDocsComponent>;
  let store: Store<AppState>;

  const mockStore = {
    dispatch: jest.fn(),
    pipe: jest.fn(),
    select: jest.fn(),
  };

  const mockLineageDocs: LineageDoc[] = [
    // Mock your LineageDoc objects here
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LineageDocsComponent],
      providers: [
        {provide: Store, useValue: mockStore},
        {provide: LineageDocsService, useClass: MockLineageDocsService}, // Replace with your LineageDocsService mock
      ],
      imports: [FormsModule, ReactiveFormsModule, ButtonModule, RippleModule, TranslocoTestingModule, DropdownModule, TooltipModule, SubsystemIframeModule, HdCommonModule, TableModule],
    });

    fixture = TestBed.createComponent(LineageDocsComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(Store);

    mockStore.select.mockReturnValue(of(mockLineageDocs)); // Mock the select method to return an Observable with mock data

    fixture.detectChanges();
  });

  it('should create the LineageDocsComponent', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch CreateBreadcrumbs on initialization', () => {
    expect(mockStore.dispatch).toHaveBeenCalledWith(
      new CreateBreadcrumbs([
        {
          label: naviElements.lineageDocsList.label,
          routerLink: naviElements.lineageDocsList.path,
        },
      ])
    );
  });

  it('should dispatch Navigate when openLineage is called', () => {
    const mockProjectDoc: LineageDoc = {
      name: 'mockProjectDoc',
      path: 'mockProjectDocPath',
      contextKey: 'mockProjectDocContextKey',
      contextName: 'mockProjectDocContextName',
      imgPath: 'mockProjectDocImgPath',
      alt: 'mockProjectDocAltText',
      lastModifiedTime: 'mockProjectDocLastModifiedTime',
    };

    component.openLineage(mockProjectDoc);

    const expectedDocLink = `/${naviElements.lineageDocs.path}/detail/${mockProjectDoc.contextKey}/${mockProjectDoc.name}/${encodeURIComponent(mockProjectDoc.path)}`;
    expect(mockStore.dispatch).toHaveBeenCalledWith(navigate({url: expectedDocLink}));
  });
});

class MockLineageDocsService {

}
