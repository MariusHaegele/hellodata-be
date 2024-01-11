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

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FaqListComponent} from './faq-list.component';
import {TranslocoModule} from "@ngneat/transloco";
import {DeleteFaqPopupComponent} from './delete-faq-popup/delete-faq-popup.component';
import {FaqEditComponent} from './faq-edit/faq-edit.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ToolbarModule} from "primeng/toolbar";
import {ButtonModule} from "primeng/button";
import {ToastModule} from "primeng/toast";
import {RippleModule} from "primeng/ripple";
import {TableModule} from "primeng/table";
import {EditorModule} from "primeng/editor";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {FieldsetModule} from "primeng/fieldset";
import {SplitButtonModule} from "primeng/splitbutton";
import {DropdownModule} from "primeng/dropdown";


@NgModule({
  declarations: [
    FaqListComponent,
    DeleteFaqPopupComponent,
    FaqEditComponent
  ],
  imports: [
    CommonModule,
    TranslocoModule,
    ReactiveFormsModule,
    ToolbarModule,
    ButtonModule,
    ToastModule,
    RippleModule,
    TableModule,
    EditorModule,
    FormsModule,
    ConfirmDialogModule,
    FieldsetModule,
    SplitButtonModule,
    DropdownModule,
  ]
})
export class FaqManagementModule {
}
