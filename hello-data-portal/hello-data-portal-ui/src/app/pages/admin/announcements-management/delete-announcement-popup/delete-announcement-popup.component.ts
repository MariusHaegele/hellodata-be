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

import {Component, Input} from '@angular/core';
import {Action, select, Store} from "@ngrx/store";
import {combineLatest, Observable, tap} from "rxjs";
import {AppState} from "../../../../store/app/app.state";
import {selectSelectedAnnouncementForDeletion} from "../../../../store/announcement/announcement.selector";
import {HideDeleteAnnouncementPopup} from "../../../../store/announcement/announcement.action";
import {ConfirmationService} from "primeng/api";
import {TranslateService} from "../../../../shared/services/translate.service";

@Component({
  selector: 'app-delete-announcement-popup[action]',
  templateUrl: './delete-announcement-popup.component.html',
  styleUrls: ['./delete-announcement-popup.component.scss']
})
export class DeleteAnnouncementPopupComponent {
  @Input()
  action!: Action;
  announcementToBeDeleted$: Observable<any>;

  constructor(private store: Store<AppState>, private confirmationService: ConfirmationService, private translateService: TranslateService) {
    this.announcementToBeDeleted$ = combineLatest([
      this.store.pipe(select(selectSelectedAnnouncementForDeletion)),
      this.translateService.selectTranslate('@Delete announcement question')
    ]).pipe(tap(([announcementForDeletion, msg]) => {
      if (announcementForDeletion) {
        this.confirmDeletion(msg);
      } else {
        this.hideDeletionPopup();
      }
    }));
  }

  deleteAnnouncement() {
    this.store.dispatch(this.action);
  }

  hideDeletionPopup(): void {
    this.store.dispatch(new HideDeleteAnnouncementPopup());
  }

  private confirmDeletion(msg: string) {
    this.confirmationService.confirm({
      message: msg,
      icon: 'fas fa-triangle-exclamation',
      accept: () => {
        this.deleteAnnouncement();
      },
      reject: () => {
        this.hideDeletionPopup();
      }
    });
  }


}
