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
import {SubscriptionsComponent} from './subscriptions.component';
import {Store} from '@ngrx/store';
import {of} from 'rxjs';
import {Context} from '../../../../store/users-management/context-role.model';
import {LoadAvailableContexts} from '../../../../store/users-management/users-management.action';
import {afterEach, beforeEach, describe, expect, it, jest} from "@jest/globals";
import {TranslocoTestingModule} from "@ngneat/transloco";

describe('SubscriptionsComponent', () => {
  let component: SubscriptionsComponent;
  let fixture: ComponentFixture<SubscriptionsComponent>;

  // Mock Store
  const mockStore = {
    dispatch: jest.fn(),
    select: jest.fn(),
    pipe: jest.fn(),
    transloco: jest.fn(),
  };

  // Sample data for businessDomains and dataDomains
  const businessDomains: Context[] = [
    {id: '1', name: 'Business Domain 1', extra: false},
    {id: '2', name: 'Business Domain 2', extra: false},
  ];
  const dataDomains: Context[] = [
    {id: '3', name: 'Data Domain 1', extra: false},
    {id: '4', name: 'Data Domain 2', extra: false},
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SubscriptionsComponent],
      imports: [TranslocoTestingModule],
      providers: [{provide: Store, useValue: mockStore}],
    });

    fixture = TestBed.createComponent(SubscriptionsComponent);
    component = fixture.componentInstance;

    // Mock store.select calls to return sample data
    jest.spyOn(mockStore, 'select').mockReturnValueOnce(of(businessDomains)).mockReturnValueOnce(of(dataDomains));

    fixture.detectChanges();
  });

  it('should create the SubscriptionsComponent', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch LoadAvailableContexts action on initialization', () => {
    expect(mockStore.dispatch).toHaveBeenCalledWith(new LoadAvailableContexts());
  });

  it('should set businessDomains$ and dataDomains$ from store', (done: any) => {
    component.businessDomains$.subscribe((businessDomainsResult) => {
      expect(businessDomainsResult).toEqual(businessDomains);

      component.dataDomains$.subscribe((dataDomainsResult) => {
        expect(dataDomainsResult).toEqual(dataDomains);
        done(); // Notify Jest that the asynchronous test is complete
      });
    });
  })

  afterEach(() => {
    // Clean up
    fixture.destroy();
  });
});
