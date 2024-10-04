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
import {Store} from "@ngrx/store";
import {AppState} from "../../../store/app/app.state";
import {selectDocumentation} from "../../../store/summary/summary.selector";
import {combineLatest, map, Observable, tap} from "rxjs";
import {naviElements} from "../../../app-navi-elements";
import {markUnsavedChanges} from "../../../store/unsaved-changes/unsaved-changes.actions";
import {BaseComponent} from "../../../shared/components/base/base.component";
import {createBreadcrumbs} from "../../../store/breadcrumb/breadcrumb.action";
import {createOrUpdateDocumentation, loadDocumentation} from "../../../store/summary/summary.actions";
import {selectSelectedLanguage, selectSupportedLanguages} from "../../../store/auth/auth.selector";
import {Documentation} from "../../../store/summary/summary.model";

@Component({
  selector: 'app-documentation',
  templateUrl: './documentation-management.component.html',
  styleUrls: ['./documentation-management.component.scss']
})
export class DocumentationManagementComponent extends BaseComponent implements OnInit {
  documentation: Documentation = {
    texts: {}
  };
  documentation$: Observable<any>;
  selectedLanguage$: Observable<string | null>;
  supportedLanguages$: Observable<string[]>;

  constructor(private store: Store<AppState>) {
    super();
    this.store.dispatch(loadDocumentation());
    this.documentation$ = combineLatest([
      this.store.select(selectDocumentation),
      this.store.select(selectSupportedLanguages)
    ]).pipe(
      tap(([doc, supportedLanguages]) => {
        let docCpy: Documentation;
        if (doc) {
          docCpy = {...doc} as Documentation;
          docCpy.texts = {...doc.texts};
        } else {
          docCpy = {texts: {}};
        }
        if (!docCpy.texts || Object.keys(docCpy.texts).length === 0) {
          supportedLanguages.forEach((language) => {
            docCpy.texts = {};
            docCpy.texts[language] = '';
          });
        }
        this.documentation = docCpy;
      }),
      map(([doc]) => doc)
    );
    this.selectedLanguage$ = this.store.select(selectSelectedLanguage);
    this.supportedLanguages$ = this.store.select(selectSupportedLanguages);
    this.createBreadcrumbs();
  }

  override ngOnInit(): void {
    super.ngOnInit();
  }

  createOrUpdateDocumentation() {
    this.store.dispatch(createOrUpdateDocumentation({
      documentation: this.documentation as Documentation
    }));
  }

  onTextChange() {
    this.store.dispatch(markUnsavedChanges({
      action: createOrUpdateDocumentation({
        documentation: this.documentation as Documentation
      })
    }));
  }

  private createBreadcrumbs() {
    this.store.dispatch(createBreadcrumbs({
      breadcrumbs: [
        {
          label: naviElements.documentationManagement.label,
          routerLink: naviElements.documentationManagement.path,
        }
      ]
    }));
  }

}
