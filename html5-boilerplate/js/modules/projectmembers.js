function MembersClass() {

    this.valuation_data = { application: { is_app_released: true }, company: {}};
    this.selectFields = {
        valuation_type: 1,
        is_app_released: 1,
        analyze_app_potential: 1,
        analyze_company_potential: 1
    };
    this.yesNoFields = {
        is_app_released: 1,
        analyze_app_potential: 1,
        analyze_company_potential: 1
    };
    this.currencyFields = {
        cost_to_duplicate: 1,
        current_revenue: 1,
        revenue_per: 1,
        cost_of_app: 1,
        best_month: 1,
        monthly_arpu: 1
    };
    this.numberFields = {
        market_size: 1,
        months_live: 1,
        target_users: 1
    };
    this.previousValuationType = null;
}

pl.implement(MembersClass, {

    store: function(listing) {
        var vdata = listing.valuation_data ? JSON.parse(listing.valuation_data) : {},
            type = vdata.valuation_type !== undefined ? vdata.valuation_type : (listing.type || 'company'),
            k, v;
        this.currency = listing.currency;
        this.valuation_data.valuation_type = type;
        if (vdata) {
            for (k in vdata) {
                v = vdata[k];
                this.valuation_data[k] = v;
            }
        }
    },

    displayActivePanel: function() {
        var activeValuationWrapperSel = '#valuation_' + this.valuation_data.valuation_type + '_wrapper';
        //if (this.previousValuationType !== this.valuation_data.vaulation_type) {
        pl('.valuationpanel').hide();
        //}
        switch (this.valuation_data.valuation_type) {
            case 'company':
                this.valueCompany();
                break;
            case 'application':
                this.valueApplication();
                break;
            default:
                this.valueCompany();
        }
        //if (this.previousValuationType !== this.valuation_data.vaulation_type) {
        this.previousValuationType = this.valuation_data.valuation_type;
        pl(activeValuationWrapperSel).show();
        //}
    },

    valueCompany: function() {
        var val = this.valuation_data.company,
            discount_rate = 0.1,
            penetration_rate = 1, // domination
            profit_margin = 0.3,
            exit_probability = 0.1,
            exit_year = 7,
            ps_ratio = 7,
            development_stage_map = {
                concept: 250000,
                team: 500000,
                product: 1000000,
                customers: 2000000,
                profitability: 5000000
            },
            current_value,
            development_stage_value,
            target_market,
            exit_value,
            npv_exit_value,
            npv_exit_value_risk_adjusted,
            company_valuation,
            num_valuations;

        development_stage_value = development_stage_map[val.development_stage];
        current_value = val.current_revenue * ps_ratio;
        target_market = val.market_size * penetration_rate;
        exit_value = target_market * val.revenue_per * profit_margin * ps_ratio;
        npv_exit_value = exit_value * Math.pow((1 - discount_rate), exit_year);
        npv_exit_value_risk_adjusted = npv_exit_value * exit_probability;
        pl('#exit_value').text(CurrencyClass.prototype.format(Math.floor(exit_value), this.currency));

        company_valuation = current_value || 0;
        num_valuations = 1;
        if (development_stage_value) {
            company_valuation += development_stage_value;
            num_valuations++;
        }
        if (val.cost_to_duplicate) {
            company_valuation += val.cost_to_duplicate;
            num_valuations++;
        }
        if (val.analyze_company_potential && npv_exit_value_risk_adjusted) {
            company_valuation += npv_exit_value_risk_adjusted;
            num_valuations++;
        }
        if (num_valuations) {
            company_valuation /= num_valuations;
        }
        company_valuation = Math.max(company_valuation, val.current_revenue); // prevent less than current 
        pl('#company_valuation').text(CurrencyClass.prototype.format(Math.floor(company_valuation), this.currency));
    },

    valueApplication: function() {
        var val = this.valuation_data.application,
            monthly_growth_rate = 0.35,
            monthly_decline_rate = 0.07,
            discount_rate = 0.1,
            exit_probability = 0.1,
            months_to_best, n, r, growth_ratio,
            future_peak, projected_peak,
            growth_sum_ratio, earnings_to_peak,
            decline_sum_ratio, earnings_after_peak,
            future_earnings,
            monthly_target,
            target_valuation,
            npv,
            npv_risk_adjusted,
            application_valuation,
            num_valuations;

        months_to_best = Math.max(Math.floor(6 - val.months_live), 0);
        n = months_to_best;
        r = 1 + monthly_growth_rate;
        growth_ratio = Math.pow(r, n);
        future_peak = val.best_month * growth_ratio;
        projected_peak = months_to_best > 0 ? future_peak : val.best_month;
        pl('#projected_peak').text(CurrencyClass.prototype.format(Math.floor(projected_peak), this.currency));

        if (months_to_best > 0) {
            growth_sum_ratio = (1 - Math.pow(r, (n+1))) / (1 - r);
            earnings_to_peak = projected_peak * growth_sum_ratio;
        }
        else {
            earnings_to_peak = projected_peak;
        }
        r = 1 - monthly_decline_rate;
        decline_sum_ratio = 1 / (1 - r);
        earnings_after_peak = projected_peak * decline_sum_ratio;
        future_earnings = earnings_to_peak + earnings_after_peak - projected_peak; // don't double count
        pl('#future_earnings').text(CurrencyClass.prototype.format(Math.floor(future_earnings), this.currency));

        monthly_target = val.target_users * val.monthly_arpu;
        pl('#monthly_target').text(CurrencyClass.prototype.format(Math.floor(monthly_target), this.currency));

        target_valuation = (monthly_target * 12) / discount_rate;
        pl('#target_valuation').text(CurrencyClass.prototype.format(Math.floor(target_valuation), this.currency));
        npv = target_valuation * Math.pow((1 - discount_rate), 2);
        npv_risk_adjusted = npv * exit_probability;

        application_valuation = future_earnings || 0;
        num_valuations = 1;
        if (val.cost_of_app) {
            application_valuation += val.cost_of_app;
            num_valuations++;
        }
        if (val.analyze_app_potential && npv_risk_adjusted) {
            application_valuation += npv_risk_adjusted;
            num_valuations++;
        }
        if (num_valuations) {
            application_valuation /= num_valuations;
        }
        application_valuation = Math.max(application_valuation, future_earnings); // prevent less than future earnings
        pl('#application_valuation').text(CurrencyClass.prototype.format(Math.floor(application_valuation), this.currency));
    },

    displayIsAppReleased: function() {
        if (this.valuation_data.application.is_app_released) {
            pl('#is_app_released_wrapper').show();
        }
        else {
            pl('#is_app_released_wrapper').hide();
        }
    },

    displayAnalyzeAppPotential: function() {
        if (this.valuation_data.application.analyze_app_potential) {
            pl('#analyze_app_potential_wrapper').show();
        }
        else {
            pl('#analyze_app_potential_wrapper').hide();
        }
    },

    displayAnalyzeCompanyPotential: function() {
        if (this.valuation_data.company.analyze_company_potential) {
            pl('#analyze_company_potential_wrapper').show();
        }
        else {
            pl('#analyze_company_potential_wrapper').hide();
        }
    }

});

function ListingMembersClass() {
    this.listing_id = (new QueryStringClass()).vars.id;
    this.base = new NewListingBaseClass();
    this.bound = {};
    this.valuation = new MembersClass();
}
pl.implement(ListingMembersClass, {
    load: function() {
        var self = this,
            url = this.listing_id
                ? '/listing/get/' + this.listing_id
                : '/listings/create',

            complete = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
                self.valuation.store(listing);
                self.display();
                pl('.preloader').hide();
                pl('.wrapper').show();
            },

            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader, .companyheader').hide();
                pl('.errorwrapper').show();
            },

            ajax = new AjaxClass(url, 'newlistingmsg', complete, null, null, error);

        if (url === '/listings/create') {
            ajax.setPost();
        }
        ajax.call();
    },

    display: function() {
        pl('#listingtype').text(this.base.listing.type.toUpperCase());
        this.displayValuationData();
        this.valuation.displayIsAppReleased();
        this.valuation.displayAnalyzeAppPotential();
        this.valuation.displayAnalyzeCompanyPotential();
        this.loadValuesFromInput();
        this.valuation.displayActivePanel();
        this.base.bindBackButton();
        this.base.bindPreviewButton();
        this.bindSaveButton();
        this.bindValuationTypeSelect();
        this.bindIsAppReleasedSelect();
        this.bindAnalyzeAppPotentialSelect();
        this.bindAnalyzeCompanyPotentialSelect();
        this.bindPanels();
    },

    displayValuationData: function() {
        var vdata = this.valuation.valuation_data;
        this.displaySelectField('valuation_type', vdata.valuation_type);
        this.displayFieldMap(vdata.application);
        this.displayFieldMap(vdata.company);
    },

    displayFieldMap: function(map) {
        var k, v;
        for (k in map) {
            v = map[k];
            if (k in this.valuation.selectFields) {
                this.displaySelectField(k, v);
            }
            else if (k in this.valuation.currencyFields) {
                this.displayCurrencyField(k, v);
            }
            else if (k in this.valuation.numberFields) {
                this.displayNumberField(k, v);
            }
            else {
                this.displayTextField(k, v);
            }
        }
    },

    displaySelectField: function(id, val) {
        return this.valuation.yesNoFields[id] ? this.displayBooleanSelectField(id, val) : this.displayRegularSelectField(id, val);
    },

    displayRegularSelectField: function(id, val) {
        var field = pl('#' + id).get(0),
            options = field.options,
            option,
            i;
        for (i = 0; i < options.length; i++) {
            option = options[i];
            if (option.value == val) {
                field.selectedIndex = i;
                break;
            }
        }
    },

    displayBooleanSelectField: function(id, val) {
        var isselected = val !== undefined && (val === true || val === 'true') ? true : false,
            field = pl('#' + id).get(0);
        if (isselected) {
            field.selectedIndex = 0;
        }
        else {
            field.selectedIndex = 1;
        }
    },

    displayCurrencyField: function(id, val) {
        pl('#' + id).attr('value', val !== undefined ? CurrencyClass.prototype.format(val, this.valuation.currency) : '');
    },

    displayNumberField: function(id, val) {
        pl('#' + id).attr('value', val !== undefined ? NumberClass.prototype.formatText(val) : '');
    },

    displayTextField: function(id, val) {
        pl('#' + id).attr('value', SafeStringClass.prototype.htmlEntities(val.toString()));
    },

    bindSaveButton: function() {
        var self = this;
        this.base.bindSaveButton(function() {
            var data = {
                    listing: {
                        id: self.listing_id,
                        valuation_data: JSON.stringify(self.valuation.valuation_data)
                    }
                },
                complete = function(json) {
                    pl('#savebuttonspinner').hide();
                    pl('#savebutton').text('@lang_saved_changes@').show();
                    setTimeout(function() {
                        pl('#savebutton').text('@lang_save@').show();
                    }, 1000);
                },
                ajax = new AjaxClass('/listing/update_field/.json', 'newlistingmsg', complete);
            pl('#savebutton').hide();
            pl('#savebuttonspinner').show();
            ajax.setPostData(data);
            ajax.call();
            return false;
        });
    },

    getSelectedValuationType: function() {
        var selectfield = pl('#valuation_type').get(0),
            options = selectfield.options,
            selectedindex = selectfield.selectedIndex,
            option = selectedindex >= 0 ? options[selectedindex] : null,
            value = option ? option.value : 'company';
        return value;
    },

    isAppReleasedSelected: function() {
        return this.isBooleanSelectSelected('#is_app_released');
    },

    isAnalyzeAppPotentialSelected: function() {
        return this.isBooleanSelectSelected('#analyze_app_potential');
    },

    isAnalyzeCompanyPotentialSelected: function() {
        return this.isBooleanSelectSelected('#analyze_company_potential');
    },

    isBooleanSelectSelected: function(sel) {
        var selectfield = pl(sel).get(0),
            options = selectfield.options,
            selectedindex = selectfield.selectedIndex,
            option = selectedindex >= 0 ? options[selectedindex] : null,
            value = option && option.value && option.value === 'true' ? true : false;
        return value;
    },

    loadValuesFromInput: function() {
        var companyval = this.valuation.valuation_data.company,
            appval = this.valuation.valuation_data.application;

        companyval.market_size = Math.max(NumberClass.prototype.clean(pl('#market_size').attr('value')), 0);
        companyval.revenue_per = Math.max(CurrencyClass.prototype.clean(pl('#revenue_per').attr('value')), 0);
        companyval.current_revenue = Math.max(CurrencyClass.prototype.clean(pl('#current_revenue').attr('value')), 0);
        companyval.cost_to_duplicate = Math.max(Math.floor(NumberClass.prototype.clean(pl('#cost_to_duplicate').attr('value'))), 0);
        companyval.development_stage = pl('#development_stage').get(0)[pl('#development_stage').get(0).selectedIndex || 0].value;

        appval.cost_of_app = Math.max(NumberClass.prototype.clean(pl('#cost_of_app').attr('value')), 0);
        appval.months_live = Math.max(NumberClass.prototype.clean(pl('#months_live').attr('value')), 0);
        appval.best_month = Math.max(NumberClass.prototype.clean(pl('#best_month').attr('value')), 0);
        appval.target_users = Math.max(NumberClass.prototype.clean(pl('#target_users').attr('value')), 0);
        appval.monthly_arpu = Math.max(NumberClass.prototype.clean(pl('#monthly_arpu').attr('value')), 0);
    },

    bindIsAppReleasedSelect: function() {
        var self = this;
        pl('#is_app_released').bind('change', function() {
            self.valuation.valuation_data.application.is_app_released = self.isAppReleasedSelected();
            self.valuation.displayIsAppReleased();
            return false;
        });
    },

    bindAnalyzeAppPotentialSelect: function() {
        var self = this;
        pl('#analyze_app_potential').bind('change', function() {
            self.valuation.valuation_data.application.analyze_app_potential = self.isAnalyzeAppPotentialSelected();
            self.valuation.displayAnalyzeAppPotential();
            return false;
        });
    },

    bindAnalyzeCompanyPotentialSelect: function() {
        var self = this;
        pl('#analyze_company_potential').bind('change', function() {
            self.valuation.valuation_data.company.analyze_company_potential = self.isAnalyzeCompanyPotentialSelected();
            self.valuation.displayAnalyzeCompanyPotential();
            return false;
        });
    },

    bindValuationTypeSelect: function() {
        var self = this;
        pl('#valuation_type').bind('change', function() {
            self.valuation.valuation_data.valuation_type = self.getSelectedValuationType();
            self.valuation.displayActivePanel();
            return false;
        });
    },

    bindPanels: function() {
        var self = this,
            evaluate = function() {
                self.loadValuesFromInput();
                self.valuation.displayActivePanel();
                return false;
            };
        pl('.valuationinput').bind({
            focus: evaluate,
            blur: evaluate,
            keyup: evaluate,
            change: evaluate
        });
    }

});


function MemberPageClass() {
    this.listing_id = (new QueryStringClass()).vars.id;
    this.members = new MembersClass();
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
    },

    hideOwnerSections: function() {
        var self = this;
        pl('#contributions_wrapper').hide();
        pl('#add_member_wrapper').hide();
        pl('#members_wrapper').hide();
    },

    displayMember: function() {
        var self = this;
        self.displayMembers();
        self.displayContributions();
        pl('#contributions_wrapper').show();
        pl('#members_wrapper').show();
    },

    bindEnableButton: function() {
        var self = this;
        pl('#enablecontributionbtn').bind('click', function() {
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
        pl('#disablecontributionbtn').bind('click', function() {
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
        pl('#addmemberbtn').bind('click', function(e) {
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
        pl('.delete-button').bind('click', function() {
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
        self.displayTotalContributions();        
        self.displaySubmittedContributions();        
        self.displayLastContributions();        
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
    },

    displayLastContributions: function()  {
        console.log('displayLastContributions()');
    },

    setContributions: function(json) {
        var self = this;
        self.submitted_contributions = json.submitted_contributions || [];
        self.last_contributions = json.last_contributions || [];
        self.total_contributions = json.total_contributions || [];
    },

    displayValuationData: function() {
        var vdata = this.members.valuation_data;
        this.displaySelectField('valuation_type', vdata.valuation_type);
        this.displayFieldMap(vdata.application);
        this.displayFieldMap(vdata.company);
    },

    displayFieldMap: function(map) {
        var k, v;
        for (k in map) {
            v = map[k];
            if (k in this.members.selectFields) {
                this.displaySelectField(k, v);
            }
            else if (k in this.members.currencyFields) {
                this.displayCurrencyField(k, v);
            }
            else if (k in this.members.numberFields) {
                this.displayNumberField(k, v);
            }
            else {
                this.displayTextField(k, v);
            }
        }
    },

    displaySelectField: function(id, val) {
        var yesnoval = val ? 'yes' : 'no',
            useval = this.members.yesNoFields[id] ? yesnoval : val,
            displayVal = useval ? SafeStringClass.prototype.ucfirst(useval.toString()) : '';
        pl('#' + id).text(displayVal);
    },

    displayCurrencyField: function(id, val) {
        pl('#' + id).text(val !== undefined ? CurrencyClass.prototype.format(val, this.members.currency) : '');
    },

    displayNumberField: function(id, val) {
        pl('#' + id).text(val !== undefined ? NumberClass.prototype.formatText(val) : '');
    },

    displayTextField: function(id, val) {
        pl('#' + id).text(SafeStringClass.prototype.htmlEntities(val.toString()));
    }

});

