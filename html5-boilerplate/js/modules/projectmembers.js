
function MemberPageClass() {
    this.listing_id = (new QueryStringClass()).vars.id;
}
pl.implement(MemberPageClass, {
    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('model');
                pl('.preloader').hide();
                header.setLogin(json);
                companybanner.display(json);
                self.store(json);
                self.display();
            },
            error = function(errornum, json) {
                pl('.preloader, .companyheader').hide();
                pl('.errorwrapper').show();
                (new HeaderClass()).setLogin(json);
            },
            ajax = new AjaxClass('/listing/contributions/' + this.listing_id, 'memberspagemsg', complete, null, null, error);
        ajax.call();
    },

    store: function(json) {
        var self = this;
        self.listing = json.listing;
        self.profile = json.loggedin_profile;
        self.setContributions(json);
    },

    display: function() {
        var self = this;
        if (!self.listing) {
            document.location = "/error-page.html";
        }
        else if (!self.profile) {
            self.displayDisabledMember();
        }
        else if (self.profile.profile_id === self.listing.profile_id && !self.listing['has_contributions']) {
            self.displayDisabledOwner(self.listing, self.profile);
        }
        else if (self.profile.profile_id === self.listing.profile_id && self.listing['has_contributions']) {
            self.displayOwner(self.listing, self.profile);
        }
        else if (self.listing['has_contributions'] && self.listing['is_contributor']) {
            self.displayMember(self.listing, self.profile);
        }
        else if (!self.listing['has_contributions']) {
            self.displayDisabledMember();
        }
        else if (!self.listing['is_contributor']) {
            self.displayDisabledMember();
        }
        else {
            document.location = "/error-page.html";
        }
    },

    displayDisabledMember: function() {
        var self = this;
        pl('#no_contributions_member').show();
    },

    displayDisabledOwner: function() {
        var self = this;
        self.bindEnableButton();
        self.showDisabledOwnerSections();
    },

    showDisabledOwnerSections: function() {
        var self = this;
        pl('#no_contributions_owner').show();
    },

    hideDisabledOwnerSections: function() {
        var self = this;
        pl('#no_contributions_owner').hide();
    },

    displayOwner: function() {
        var self = this;
        //this.members.store(listing, profile);
        //this.displayValuationData();
        //this.members.displayActivePanel();
        self.bindDisableButton();
        self.bindSaveButton();
        self.showOwnerSections();
    },

    showOwnerSections: function() {
        var self = this;
        self.displayMembers();
        self.displayContributions();
        self.bindMemberAutocomplete();
        self.bindAddMemberButton();
        pl('#contributions_wrapper').show();
        pl('#add_member_wrapper').show();
        pl('#members_wrapper').show();
        pl('#disablecontributionbtn').show();
        pl('#savecontributionbtn').show();
    },

    hideOwnerSections: function() {
        var self = this;
        pl('#contributions_wrapper').hide();
        pl('#add_member_wrapper').hide();
        pl('#members_wrapper').hide();
        pl('#disablecontributionbtn').hide();
        pl('#savecontributionbtn').hide();
    },

    displayMember: function() {
        var self = this;
        self.displayMembers();
        self.displayContributions();
        pl('#contributions_wrapper').show();
        pl('#members_wrapper').show();
        pl('#add_member_wrapper').hide();
        pl('#disablecontributionbtn').hide();
        pl('#savecontributionbtn').hide();
    },

    bindEnableButton: function() {
        var self = this;
        pl('#enablecontributionbtn').unbind('click').bind('click', function() {
            var data = {
                    listing: {
                        id: self.listing.listing_id,
                        has_contributions: true
                    }
                },
                completeFunc = function(json) {
                    pl('#enablecontributionspinner').hide();
                    self.hideDisabledOwnerSections();
                    self.listing['has_contributions'] = true;
                    self.displayOwner();
                },
                errorFunc = function(errornum, json) {
                    pl('#enablecontributionspinner').hide();
                },
                ajax = new AjaxClass('/listing/update_field', 'enablecontributionmsg', null, completeFunc, null, errorFunc);
            pl('#enablecontributionspinner').show();
            ajax.setPostData(data);
            ajax.call();
        });
    },

    bindDisableButton: function() {
        var self = this;
        pl('#disablecontributionbtn').unbind('click').bind('click', function() {
            var data = {
                    listing: {
                        id: self.listing.listing_id,
                        has_contributions: false
                    }
                },
                completeFunc = function(json) {
                    pl('#disablecontributionspinner').hide();
                    self.hideOwnerSections();
                    self.listing['has_contributions'] = false;
                    self.displayDisabledOwner();
                },
                errorFunc = function(errornum, json) {
                    pl('#disablecontributionspinner').hide();
                },
                ajax = new AjaxClass('/listing/update_field', 'disablecontributionmsg', null, completeFunc, null, errorFunc);
            pl('#disablecontributionspinner').show();
            ajax.setPostData(data);
            ajax.call();
        });
    },


    bindSaveButton: function() {
        var self = this;
        pl('#savecontributionbtn').unbind('click').bind('click', function() {
            var hourly_rate = NumberClass.prototype.clean(pl('#hourlyrate').get(0).value),
                interest_rate = NumberClass.prototype.clean(pl('#interestrate').get(0).value),
                data = {
                    listing: {
                        id: self.listing.listing_id,
                        contribution_per_hour: hourly_rate,
                        contribution_interest_daily: interest_rate
                    }
                },
                success = function(json) {
                    pl('#savecontributionspinner').hide();
                    self.listing['contribution_per_hour'] = hourly_rate;
                    self.listing['contribution_interest_daily'] = interest_rate;
                    self.displayRateFields();
                    self.load();
                },
                errorFunc = function(errornum, json) {
                    pl('#savecontributionspinner').hide();
                },
                ajax = new AjaxClass('/listing/update_field', 'disablecontributionmsg', null, success, null, errorFunc);
            pl('#savecontributionspinner').show();
            ajax.setPostData(data);
            ajax.call();
        });
    },

    bindMemberAutocomplete: function() {
        var self = this,
            eid = '#addmembertext',
            displayOptions = {
                fontSize : '14px',
                fontFamily : 'Lucida Grande, sans-serif',
                color:'#49515a'
            };
        self.auto = completely(pl('#addmemberauto').get(0), displayOptions);
        self.auto.onChange = function(txt) {
            if (txt === null || txt.length < 1) {
                self.auto.options = [];
                self.auto.repaint();
            }
            else {
                self.fillAutocomplete(txt);
            }
        };
        self.auto.repaint();
    },

    fillAutocomplete: function(txt) {
        var self = this,
            success = function(json) {
                var users = json ? json.users : null,
                    count = users ? users.length : 0,
                    options = [],
                    user, username, i;
                if (count == 0) {
                    self.auto.options = [''];
                }
                else {
                    self.automap = {};
                    for (i = 0; i < users.length; i++) {
                        user = users[i];
                        if (!user || !user.profile_id || !user.username
                            || user.profile_id === self.listing.profile_id) {
                            continue;
                        }
                        username = user.username.toLowerCase();
                        options.push(username);
                        self.automap[username] = user.profile_id;
                    }
                    self.auto.options = options;
                }
                console.log('set options', options);
                self.auto.repaint();
            };
        ajax = new AjaxClass('/user/find.json?query=' + txt, '', null, success, null, null);
        ajax.call();
    },

    bindAddMemberButton: function() {
        var self = this;
        pl('#addmemberbtn').unbind('click').bind('click', function(e) {
            var username = self.auto.getText(),
                user_id = self.automap[username],
                data = {
                    id: self.listing.listing_id,
                    user_id: user_id
                },
                success = function(json) {
                    pl('#addmemberspinner').hide();
                    pl('#addmembermsg').get(0).innerText = '';
                    self.auto.setText('');
                    self.setContributions(json);
                    self.displayMembers();
                    self.displayContributions();
                },
                error = function(errornum, json) {
                    pl('#addmembermsg').get(0).innerText = '@lang_username_not_found@';
                    pl('#addmemberspinner').hide();
                },
                ajax = new AjaxClass('/listing/add_contributor', 'addmembermsg', null, success, null, error);
            if (user_id) {
                pl('#addmembermsg').get(0).innerText = '';
                pl('#addmemberspinner').show();
                ajax.setPostData(data);
                ajax.call();
            }
            else {
                pl('#addmembermsg').get(0).innerText = '@lang_username_not_found@';
            }
            e.preventDefault();
        });
    },

    displayMembers: function() {
        var self = this,
            html = '',
            member,
            i;
        console.log('displayMembers() total=', self.total_contributions);
        html += '<table>\n<tbody>\n';
        html += '<tr><th></th><th></th></tr>\n';
        for (i = 0; i < self.total_contributions.length; i++) {
            member = self.total_contributions[i];
            console.log('displayMembers() member=', member);
            if (member == null || !member.contributor_id || !member.contributor_username) {
                continue;
            }
            html += '<tr>\n';
            if (member.contributor_id === self.listing.profile_id) {
                html += '<td>' + member.contributor_username + ' <b>@lang_you@</b></td>\n';
                html += '<td></td>';
            }
            else if (member.contributor_id === self.listing.profile_id) {
                html += '<td>' + member.contributor_username + ' <b>@lang_project_owner@</b></td>\n';
                html += '<td></td>';
            }
            else if (self.listing.profile_id !== self.profile.profile_id) {
                html += '<td>' + member.contributor_username + '</td>\n';
                html += '<td></td>';
            }
            else {
                html += '<td>' + member.contributor_username + '</td>\n';
                html += '<td class="delete-cell">'
                    + '<div class="delete-button">'
                    + '<span class="delete-id">'
                    + member.contributor_id
                    + '</span>'
                    + '</div>'
                    + '</td>\n';
            }
            html += '</tr>\n';
        }
        html += '</tbody>\n<table>\n';
        pl('#memberslist').get(0).innerHTML = html;
        self.bindMemberDelete();
    },

    bindMemberDelete: function() {
        var self = this;
        pl('.delete-button').unbind('click').bind('click', function() {
            console.log('pl(this)', pl(this));
            var contributor_id = pl(this).get(0).firstChild.innerText,
                data = {
                    id: self.listing.listing_id,
                    user_id: contributor_id
                },
                success = function(json) {
                    self.setContributions(json);
                    self.displayMembers();
                    self.displayContributions();
                },
                ajax = new AjaxClass('/listing/remove_contributor', 'deletemembermsg', null, success, null, null);
            if (contributor_id) {
                ajax.setPostData(data);
                ajax.call();
            }
        });
    },

    displayContributions: function()  {
        console.log('displayContributions()');
        var self = this;
        self.displayRateFields();
        self.displayTotalContributions();
        self.displaySubmittedContributions();
        self.displayLastContributions();
        self.bindAddContribution();
        self.bindDownloadContributions();
    },

    displayRateFields: function() {
        var self = this,
            hourly_rate = CurrencyClass.prototype.format(self.listing.contribution_per_hour, self.listing.currency),
            interest_rate = NumberClass.prototype.clean(self.listing.contribution_interest_daily) + '%';
        pl('#hourlyrate').get(0).value = hourly_rate;
        pl('#interestrate').get(0).value = interest_rate;
        if (self.profile.profile_id === self.listing.profile_id) {
            if (pl('#hourlyrate').attr('disabled')) {
                pl('#hourlyrate').removeAttr('disabled');
            }
            if (pl('#interestrate').attr('disabled')) {
                pl('#interestrate').removeAttr('disabled');
            }
        }
        else {
            pl('#hourlyrate').attr('disabled', 'disabled');
            pl('#interestrate').attr('disabled', 'disabled');
        }
    },

    displayTotalContributions: function()  {
        console.log('displayTotalContributions()');
        var self = this,
            html = '',
            hours = 0,
            money = 0,
            financial = 0,
            member,
            thousandsSep = self.listing.currency === 'pln' ? ' ' : ',',
            i;
        console.log('displayTotalContributions() total=', self.total_contributions);
        html += '<table class="contribution-table">\n<tbody>\n';

        html += '<tr>\n';
        html += '<th class="contribution-member-cell">@lang_member_title@</th>\n';
        html += '<th class="contribution-cell">@lang_total_hours@</th>\n';
        html += '<th class="contribution-cell">@lang_total_money@</th>\n';
        html += '<th class="contribution-cell">@lang_financial_value@</th>\n';
        html += '</tr>\n';

        for (i = 0; i < self.total_contributions.length; i++) {
            member = self.total_contributions[i];
            console.log('displayTotalContributions() member=', member);
            if (member == null || !member.contributor_id || !member.contributor_username) {
                continue;
            }
            html += '<tr>\n';

            html += '<td class="contribution-member-cell">' + member.contributor_username;
            if (member.contributor_id === self.listing.profile_id) {
                html += ' <b>@lang_you@</b>';
            }
            else if (member.contributor_id === self.listing.profile_id) {
                html += ' <b>@lang_project_owner@</b>';
            }
            html += '</td>\n';

            html += '<td class="contribution-cell">' + member.total_hours + '</td>\n';
            hours += 1 * member.total_hours;

            html += '<td class="contribution-cell">' + CurrencyClass.prototype.format(member.total_money, self.listing.currency) + '</td>\n';
            money += 1 * member.total_money;

            html += '<td class="contribution-cell">' + CurrencyClass.prototype.format(member.financial_value, self.listing.currency) + '</td>\n';
            financial += 1 * member.financial_value;

            html += '</tr>\n';
        }

        html += '<tr>\n';
        html += '<td class="contribution-member-cell"><b>@lang_grand_total@</b></td>\n';

        html += '<td class="contribution-cell">' + NumberClass.prototype.formatText(hours, '', '', thousandsSep, '.', 1) + '</td>\n';
        html += '<td class="contribution-cell">' + CurrencyClass.prototype.format(money, self.listing.currency) + '</td>\n';
        html += '<td class="contribution-cell">' + CurrencyClass.prototype.format(financial, self.listing.currency) + '</td>\n';
        html += '</tr>\n';

        html += '</tbody>\n<table>\n';
        pl('#totalcontributionslist').get(0).innerHTML = html;
    },


    displaySubmittedContributions: function()  {
        console.log('displaySubmittedContributions()');
        var self = this,
            html = '',
            member,
            i;
        console.log('displaySubmittedContributions() total=', self.submitted_contributions);

        if (self.submitted_contributions.length == 0) {
            pl('#submittedcontributionsnotice').get(0).innerHTML = '@lang_pending_contributions_none_notice@';
            pl('#submittedcontributionslist').get(0).innerHTML = '';
            pl('#submittedcontributionstitle').show();
            pl('#submittedcontributionswrapper').show();
            return;
        }
        else if (self.profile.profile_id === self.listing.profile_id) { // listing owner
            pl('#submittedcontributionsnotice').get(0).innerText = '@lang_pending_contributions_owner_notice@';
        }
        else {
            pl('#submittedcontributionsnotice').get(0).innerText = '@lang_pending_contributions_member_notice@';
        }

        html += '<table class="contribution-table">\n<tbody>\n';

        html += '<tr>\n';
        html += '<th class="">@lang_date_title@</th>\n';
        html += '<th class="">@lang_member_short_title@</th>\n';
        html += '<th class="contribution-cell">@lang_hours_title@</th>\n';
        html += '<th class="contribution-cell">@lang_amount_title@</th>\n';
        html += '<th class="">@lang_notes_title@</th>\n';
        html += '<th class="contribution-cell">@lang_action_title@</th>\n';
        html += '</tr>\n';

        for (i = 0; i < self.submitted_contributions.length; i++) {
            member = self.submitted_contributions[i];
            console.log('displayTotalContributions() member=', member);
            if (member == null || !member.contributor_id || !member.contributor_username) {
                continue;
            }
            html += '<tr>\n';

            html += '<td class="">' + DateClass.prototype.formatDateStr(member.contribution_date) + '</td>\n';

            html += '<td class="">' + member.contributor_username;
            if (member.contributor_id === self.listing.profile_id) {
                html += ' <b>@lang_you@</b>';
            }
            else if (member.contributor_id === self.listing.profile_id) {
                html += ' <b>@lang_project_owner@</b>';
            }
            html += '</td>\n';

            html += '<td class="contribution-cell">' + member.hours + '</td>\n';

            html += '<td class="contribution-cell">' + CurrencyClass.prototype.format(member.money, self.listing.currency) + '</td>\n';

            html += '<td class="">' + (member.description || '') + '</td>\n';

            html += '<td class="approve-cell">';
            if (self.profile.profile_id === self.listing.profile_id) { // listing owner
                html += '<div class="approve-button"><span class="initialhidden">' + member.contribution_id + '</span></div>';
                html += '<div class="reject-button"><span class="initialhidden">' + member.contribution_id + '</span></div>';
            }
            else if (self.profile.profile_id === member.contributor_id) { // my own contribution
                html += '<div class="reject-button"><span class="initialhidden">' + member.contribution_id + '</span></div>';
            }
            html += '</td>';

            html += '</tr>\n';
        }

        html += '</tbody>\n<table>\n';

        pl('#submittedcontributionslist').get(0).innerHTML = html;

        self.bindApproveContribution();
        self.bindRejectContribution();

        pl('#submittedcontributionstitle').show();
        pl('#submittedcontributionswrapper').show();
    },

    bindApproveContribution: function() {
        var self = this;
        pl('.approve-button').unbind('click').bind('click', function() {
            console.log('pl(this)', pl(this));
            var contribution_id = pl(this).get(0).firstChild.innerText,
                data = {
                    contribution_id: contribution_id
                },
                success = function(json) {
                    self.setContributions(json);
                    self.displayMembers();
                    self.displayContributions();
                },
                ajax = new AjaxClass('/listing/approve_contribution', 'approvecontributionmsg', null, success, null, null);
            if (contribution_id) {
                ajax.setPostData(data);
                ajax.call();
            }
        });
    },

    bindRejectContribution: function() {
        var self = this;
        pl('.reject-button').unbind('click').bind('click', function() {
            console.log('pl(this)', pl(this));
            var contribution_id = pl(this).get(0).firstChild.innerText,
                data = {
                    contribution_id: contribution_id
                },
                success = function(json) {
                    self.setContributions(json);
                    self.displayMembers();
                    self.displayContributions();
                },
                ajax = new AjaxClass('/listing/delete_contribution', 'approvecontributionmsg', null, success, null, null);
            if (contribution_id) {
                ajax.setPostData(data);
                ajax.call();
            }
        });
    },

    displayLastContributions: function()  {
        console.log('displayLastContributions()');
    },

    bindAddContribution: function() {
        console.log('displayAddContribution()');
        var self = this;
        pl('#addcontributiondate').unbind('focus').bind('focus', function() {
            var val = pl(this).get(0).value,
                today = DateClass.prototype.formatDate(new Date(), '-');
            if (val === '@lang_date@') {
                pl(this).get(0).value = today;
            }
        });
        pl('#addcontributionhours').unbind('focus').bind('focus', function() {
            var val = pl(this).get(0).value.trim();
            if (val === '@lang_hours@') {
                pl(this).get(0).value = '';
            }
        });
        pl('#addcontributionamount').unbind('focus').bind('focus', function() {
            var val = pl(this).get(0).value.trim();
            if (val === '@lang_amount@') {
                pl(this).get(0).value = '';
            }
        });
        pl('#addcontributionnotes').unbind('focus').bind('focus', function() {
            var val = pl(this).get(0).value.trim();
            if (val === '@lang_bid_notes@') {
                pl(this).get(0).value = '';
            }
        });
        self.bindAddContributionButton();
    },

    bindAddContributionButton: function() {
        var self = this;
        pl('#addcontributionbtn').unbind('click').bind('click', function(e) {
            /*
             {"listing_id":"ahBpbndlc3R1ai13LWZpcm15cg4LEgdMaXN0aW5nGPcPDA",
             "contribution_date":"20140428", "description":"test contrib", "money":"2.48", "hours":"3.0"}
             */
            var contribution_date = DateClass.prototype.clean(pl('#addcontributiondate').get(0).value),
                description = pl('#addcontributionnotes').get(0).value.trim(),
                hours = NumberClass.prototype.clean(pl('#addcontributionhours').get(0).value) || 0,
                money = NumberClass.prototype.clean(pl('#addcontributionamount').get(0).value) || 0,
                data = {
                    contribution: {
                        listing_id: self.listing.listing_id,
                        contribution_date: contribution_date,
                        description: description,
                        hours: hours,
                        money: money
                    }
                },
                success = function(json) {
                    pl('#addcontributionspinner').hide();
                    pl('#addcontributionmsg').removeClass('errorcolor');
                    pl('#addcontributionmsg').get(0).innerText = '@lang_contribution_added@';
                    pl('#addcontributionnotes').get(0).value = '@lang_bid_notes@';
                    //pl('#addcontributiondate').get(0).value = '@lang_date@';
                    pl('#addcontributionhours').get(0).value = '@lang_hours@';
                    pl('#addcontributionamount').get(0).value = '@lang_amount@';
                    self.setContributions(json);
                    self.displayMembers();
                    self.displayContributions();
                },
                error = function(errornum, json) {
                    var errorStr = (json && json.error_msg) ? '@lang_error_from_server@: ' + json.error_msg
                        : '@lang_error_from_server@ ' + errorNum;
                    pl('#addcontributionspinner').hide();
                    pl('#addcontributionmsg').html('<span class="attention">' + errorStr + '</span>');
                },
                ajax = new AjaxClass('/listing/add_contribution', 'addcontributionmsg', null, success, null, error);
            console.log('contribution_date', contribution_date);
            console.log('hours', hours);
            console.log('money', money);
            if (contribution_date.match(/^\d{8}$/)) {
                pl('#addcontributionmsg').get(0).innerText = '';
                pl('#addcontributionspinner').show();
                ajax.setPostData(data);
                console.log('ajax', ajax.ajaxOpts.data);
                ajax.call();
            }
            else {
                pl('#addcontributionspinner').hide();
                pl('#addcontributionmsg').addClass('errorcolor');
                pl('#addcontributionmsg').get(0).innerText = '@lang_invalid_yyyymmdd_date@';
            }
            e.preventDefault();
        });
    },

    bindDownloadContributions: function() {
        var self = this;
        pl('#downloadcontributionbtn').unbind('click').bind('click', function(e) {
            var url = '/listing/download_contributions.csv?id=' + self.listing.listing_id;
            window.open(url);
            e.preventDefault();
        });
    },

    setContributions: function(json) {
        var self = this;
        self.submitted_contributions = json.submitted_contributions || [];
        self.last_contributions = json.last_contributions || [];
        self.total_contributions = json.total_contributions || [];
    }

});

