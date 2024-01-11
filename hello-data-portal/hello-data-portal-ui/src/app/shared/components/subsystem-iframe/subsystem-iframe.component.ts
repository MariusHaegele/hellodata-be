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

import {Component, ElementRef, EventEmitter, Input, NgModule, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {CommonModule} from "@angular/common";
import {HdCommonModule} from "../../../hd-common.module";
import {AuthService} from "../../services";
import {Subscription} from "rxjs";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-subsystem-iframe[url]',
  templateUrl: './subsystem-iframe.component.html',
  styleUrls: ['./subsystem-iframe.component.scss']
})
export class SubsystemIframeComponent implements OnInit, OnDestroy, OnChanges {

  @ViewChild('iframe') iframe!: ElementRef;
  @Input() url!: string;
  @Input() accessTokenInQueryParam = false;
  @Input() delay = 0;
  @Input() style: { [p: string]: any } | null = null;
  @Output() iframeSetup = new EventEmitter<boolean>();
  frameUrl!: string;

  accessTokenSub!: Subscription;

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
    console.debug('after view init', this.url, this.delay);
    this.accessTokenSub = this.authService.accessToken.subscribe({
      next: value => {
        console.debug('access token changed', value)
        console.debug("creating an auth cookie for a domain: ." + environment.baseDomain);
        document.cookie = 'auth.access_token=' + value + '; path=/; domain=.' + environment.baseDomain + '; secure;';
        setTimeout(() => {
          this.frameUrl = this.accessTokenInQueryParam ? this.url + '?auth.access_token=' + value : this.url;
          this.iframeSetup.emit(true);
        }, this.delay)
      }
    });
  }

  ngOnDestroy() {
    if (this.accessTokenSub) {
      this.accessTokenSub.unsubscribe();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ('url' in changes) {
      if (this.accessTokenSub) {
        this.accessTokenSub.unsubscribe();
      }
      this.accessTokenSub = this.authService.accessToken.subscribe({
        next: value => {
          this.frameUrl = this.url;
          this.iframeSetup.emit(true);
        }
      });
    }
  }

}

@NgModule({
  imports: [
    CommonModule,
    HdCommonModule
  ],
  declarations: [SubsystemIframeComponent],
  exports: [SubsystemIframeComponent]
})
export class SubsystemIframeModule {
}
