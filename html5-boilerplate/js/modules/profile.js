function CampaignTileClass() {}
pl.implement(CampaignTileClass, {

    makeNew: function() {
        var self = this,
            campaign_id = Math.floor(Math.random()*1000000000),
            now = new Date(),
            endTime = DateClass.prototype.addDays(now, 120),
            active_from = DateClass.prototype.formatDatetime(now),
            active_to = DateClass.prototype.formatDatetime(endTime);
        self.campaign = {
            campaign_id: campaign_id,
            active_from: active_from,
            active_to: active_to,
            allowed_languages: 'PL',
            status: 'NEW',
            name: '@lang_campaign_name@',
            description: '@lang_campaign_desc@',
            subdomain: 'campaign-' + campaign_id,
            pricepoints: []
        };
    },

    store: function(campaign) {
        var self = this;
        self.campaign = campaign;
        console.log('CampaignTileClass::store id=' + self.campaign.campaign_id);
        console.log(self.campaign.pricepoints);
    },

    formattedDateStr: function(datetimestr) {
        return DateClass.prototype.formatDatetimeStr(
                DateClass.prototype.formatDatetime(
                    DateClass.prototype.dateFromYYYYMMDDHHMMSS(datetimestr)));
    },

    formattedAllowedLang: function(lang) {
        var langStr;
        if (lang === 'EN') {
            langStr = '@lang_english@';
        }
        else if (lang === 'PL') {
            langStr = '@lang_polish@';
        }
        else {
            langStr = lang;
        }
        return langStr;
    },

    formattedStatus: function(status) {
        var statusStr;
        if (status === 'NEW') {
            statusStr = '@lang_new@';
        }
        else if (status === 'ACTIVE') {
            statusStr = '@lang_active_campaign@';
        }
        else {
            statusStr = '@lang_closed@';
        }
        return statusStr;
    },

    makeTile: function() { // uneditable
        var self = this,
            campaignUniqId = 'campaign_' + self.campaign.campaign_id,
            messageId = campaignUniqId + '_message',
            campaignUniqTitle = campaignUniqId + '_title',
            campaignUniqDesc = campaignUniqId + '_desc',
            campaignUniqActiveFrom = campaignUniqId + '_active_from',
            campaignUniqActiveTo = campaignUniqId + '_active_to',
            campaignUniqSubdomain = campaignUniqId + '_subdomain',
            campaignUniqLang = campaignUniqId + '_lang',
            campaignUniqLangEN = campaignUniqId + '_lang_en',
            campaignUniqLangPL = campaignUniqId + '_lang_pl',
            campaignUniqStatus = campaignUniqId + '_status',
            campaignUniqStatusNEW = campaignUniqId + '_status_new',
            campaignUniqStatusACTIVE = campaignUniqId + '_status_active',
            campaignUniqStatusCLOSED = campaignUniqId + '_status_closed',
            campaignStatusCheckedNEW = '',
            campaignStatusCheckedACTIVE = '',
            campaignStatusCheckedCLOSED = '',
            dateFromStr = self.formattedDateStr(self.campaign.active_from),
            dateToStr = self.formattedDateStr(self.campaign.active_to),
            langCheckedEN = '',
            langCheckedPL = '',
            langStr = self.formattedAllowedLang(self.campaign.allowed_languages),
            statusStr = self.formattedStatus(self.campaign.status),
            pricepointObj = new PricepointsClass(self.campaign.pricepoints),
            pricepointHTML = pricepointObj.buttonsHTML(self.campaign.pricepoints, 'pricepoint-description-leftcol'),
            html = '';
        console.log('CampaignClass::makeTile id=' + self.campaign.campaign_id);
        console.log(self.matchingPricepoints);
        if (self.campaign.allowed_languages === 'EN') {
            langCheckedEN = ' checked ';
        }
        else if (self.campaign.allowed_languages === 'PL') {
            langCheckedPL = ' checked ';
        }
        if (self.campaign.status === 'NEW') {
            campaignStatusCheckedNEW = ' checked ';
        }
        else if (self.campaign.status === 'ACTIVE') {
            campaignStatusCheckedACTIVE = ' checked ';
        }
        else {
            campaignStatusCheckedCLOSED = ' checked ';
        }
        html +=
              '<div class="boxpanel"'
            +     ' id="' + campaignUniqId + '" >'
            +   '<div class="campaign-item-title">'
            +     '<span>'
            +       self.campaign.name
            +     '</span>'
            +     '<input class="text campaign-input-title initialhidden" type="text"'
            +       ' name="' + campaignUniqTitle + '"'
            +       ' id="' + campaignUniqTitle + '"'
            +       ' maxlength="60"'
            +       ' value="' + self.campaign.name + '">'
            +     '</input>'
            +   '</div>'
            +   '<div class="campaign-item-description">'
            +     '<p>' + self.campaign.description + '</p>'
            +     '<textarea class="inputtextareatwoline campaign-input-description initialhidden" cols="70" rows="2"'
            +       ' name="' + campaignUniqDesc + '"'
            +       ' id="' + campaignUniqDesc + '"'
            +       ' maxlength="140">'
            +       self.campaign.description
            +     '</textarea>'
            +   '</div>'
            +   '<div class="campaign-item-active-from">'
            +       '<label class="campaign-item-label">@lang_start_date@</label>'
            +       '<span class="campaign-item-value">' + dateFromStr + '</span>'       // date field
            +       '<input class="text campaign-item-input initialhidden" type="text"'
            +         ' name="' + campaignUniqActiveFrom + '"'
            +         ' id="' + campaignUniqActiveFrom + '"'
            +         ' maxlength="20"'
            +         ' value="' + dateFromStr + '">'
            +       '</input>'
            +   '</div>'
            +   '<div class="campaign-item-active-to">'
            +       '<label class="campaign-item-label">@lang_end_date@</label>'
            +       '<span class="campaign-item-value">' + dateToStr + '</span>'         // date field
            +       '<input class="text campaign-item-input initialhidden" type="text"'
            +         ' name="' + campaignUniqActiveTo + '"'
            +         ' id="' + campaignUniqActiveTo + '"'
            +         ' maxlength="20"'
            +         ' value="' + dateToStr + '">'
            +       '</input>'
            +   '</div>'
            +   '<div class="campaign-item-language">'
            +       '<label class="campaign-item-label">@lang_language@</label>'
            +       '<span class="campaign-item-value">' + langStr + '</span>' // dropdown PL/EN
            +       '<input class="text campaign-item-radio initialhidden" type="radio"'
            +         ' name="' + campaignUniqLang + '"'
            +         ' id="' + campaignUniqLangEN + '"'
            +         langCheckedEN
            +         ' value="EN">'
            +       '</input>'
            +       '<span class="campaign-item-radio-label initialhidden">@lang_english@</span>'
            +       '<input class="text campaign-item-radio initialhidden" type="radio"'
            +         ' name="' + campaignUniqLang + '"'
            +         ' id="' + campaignUniqLangPL + '"'
            +         langCheckedPL
            +         ' value="PL">'
            +       '</input>'
            +       '<span class="campaign-item-radio-label initialhidden">@lang_polish@</span>'
            +   '</div>'
            +   '<div class="campaign-item-subdomain">'
            +       '<label class="campaign-item-label">@lang_subdomain@</label>'
            +       '<span class="campaign-item-value">' + self.campaign.subdomain + '</span>' // text field first-time only, read-only otherwise
            +       '<input class="text campaign-item-input initialhidden" type="text"'
            +         ' name="' + campaignUniqSubdomain + '"'
            +         ' id="' + campaignUniqSubdomain + '"'
            +         ' maxlength="30"'
            +         ' value="' + self.campaign.subdomain + '">'
            +       '</input>'
            +   '</div>'
            +   '<div class="campaign-item-status">'
            +       '<label class="campaign-item-label">@lang_status@</label>'
            +       '<span class="campaign-item-value">' + statusStr + '</span>'
            +       '<input class="text campaign-item-radio initialhidden" type="radio"'
            +         ' name="' + campaignUniqStatus + '"'
            +         ' id="' + campaignUniqStatusNEW + '"'
            +         campaignStatusCheckedNEW
            +         ' value="NEW">'
            +       '</input>'
            +       '<span class="campaign-item-radio-label campaign-item-status-radio-label initialhidden">@lang_new@</span>'
            +       '<input class="text campaign-item-radio initialhidden" type="radio"'
            +         ' name="' + campaignUniqStatus + '"'
            +         ' id="' + campaignUniqStatusACTIVE + '"'
            +         campaignStatusCheckedACTIVE
            +         ' value="ACTIVE">'
            +       '</input>'
            +       '<span class="campaign-item-radio-label initialhidden">@lang_active_campaign@</span>'
            +       '<input class="text campaign-item-radio initialhidden" type="radio"'
            +         ' name="' + campaignUniqStatus + '"'
            +         ' id="' + campaignUniqStatusCLOSED + '"'
            +         campaignStatusCheckedCLOSED
            +         ' value="CLOSED">'
            +       '</input>'
            +       '<span class="campaign-item-radio-label initialhidden">@lang_closed@</span>'
            +   '</div>'
            +   '<div class="campaign-message" id="' + messageId + '"></div>'
            +   '<div class="campaign-item-button investbutton campaign-edit-button">@lang_edit@</div>'
            +   '<div class="campaign-item-button investbutton campaign-undo-button initialhidden">@lang_undo@</div>'
            +   '<div class="campaign-item-button investbutton campaign-save-button initialhidden">@lang_save@</div>'
            +   pricepointHTML
            + '</div>';
        return html;
    },

    bindEvents: function(options) {
        var self = this,
            isAdmin = options.isAdmin,
            isNew = self.campaign.status === 'NEW',
            campaignUniqId = 'campaign_' + self.campaign.campaign_id,
            campaignSel = '#' + campaignUniqId,
            messageId = campaignUniqId + '_message',
            messageSel = '#' + messageId,
            editSel = campaignSel + ' div.campaign-edit-button',
            undoSel = campaignSel + ' div.campaign-undo-button',
            saveSel = campaignSel + ' div.campaign-save-button',
            titleSel = campaignSel + ' div.campaign-item-title span',
            titleInputSel = campaignSel + ' div.campaign-item-title input',
            descSel = campaignSel + ' div.campaign-item-description p',
            descInputSel = campaignSel + ' div.campaign-item-description textarea',
            activeFromSel = campaignSel + ' div.campaign-item-active-from span',
            activeFromInputSel = campaignSel + ' div.campaign-item-active-from input',
            activeToSel = campaignSel + ' div.campaign-item-active-to span',
            activeToInputSel = campaignSel + ' div.campaign-item-active-to input',
            subdomainSel = campaignSel + ' div.campaign-item-subdomain span',
            subdomainInputSel = campaignSel + ' div.campaign-item-subdomain input',
            langSel = campaignSel + ' div.campaign-item-language span.campaign-item-value',
            langInputSel = campaignSel + ' div.campaign-item-language input',
            langInputLabelSel = campaignSel + ' div.campaign-item-language span.campaign-item-radio-label',
            statusSel = campaignSel + ' div.campaign-item-status span.campaign-item-value',
            statusInputSel = campaignSel + ' div.campaign-item-status input',
            statusInputLabelSel = campaignSel + ' div.campaign-item-status span.campaign-item-radio-label',
            viewableSel = [ titleSel, descSel, activeFromSel, activeToSel, langSel, editSel ],
            editableSel = [ titleInputSel, descInputSel, activeFromInputSel, activeToInputSel,
                langInputSel, langInputLabelSel, undoSel, saveSel ],
            adminViewableSel = [ statusSel ],
            adminEditableSel = [ statusInputSel, statusInputLabelSel ],
            newViewableSel = [ subdomainSel ],
            newEditableSel = [ subdomainInputSel ]
            ;
        pl(editSel).unbind().bind({
            click: function() {
                pl(messageSel).get(0).innerText = '';
                pl(viewableSel.join(',')).hide();
                pl(editableSel.join(',')).show();
                if (isAdmin) {
                    pl(adminViewableSel.join(',')).hide();
                    pl(adminEditableSel.join(',')).show();
                }
                if (isNew) {
                    pl(newViewableSel.join(',')).hide();
                    pl(newEditableSel.join(',')).show();
                }
                return false;
            }
        });
        pl(undoSel).unbind().bind({
            click: function() {
                pl(messageSel).get(0).innerText = '';
                pl(editableSel.join(',')).hide();
                pl(viewableSel.join(',')).show();
                if (isAdmin) {
                    pl(adminEditableSel.join(',')).hide();
                    pl(adminViewableSel.join(',')).show();
                }
                if (isNew) {
                    pl(newEditableSel.join(',')).hide();
                    pl(newViewableSel.join(',')).show();
                }
                return false;
            }
        });
        pl(saveSel).unbind().bind({
            click: function() {
                var subdomain = pl(subdomainInputSel).get(0).value.replace(/[^a-z0-9-]/g, '').toLowerCase(),
                    name = pl(titleInputSel).get(0).value,
                    description = pl(descInputSel).get(0).value,
                    active_from = pl(activeFromInputSel).get(0).value.replace(/[^0-9]/g, ''),
                    active_to = pl(activeToInputSel).get(0).value.replace(/[^0-9]/g, ''),
                    allowed_languages = pl(langInputSel + '[checked]').get(0).value,
                    status = pl(statusInputSel + '[checked]').get(0).value,
                    data = {
                        campaign: {
                            subdomain: subdomain,
                            name: name,
                            description: description,
                            active_from: active_from,
                            active_to: active_to,
                            allowed_languages: allowed_languages,
                            status: status
                        }
                    },
                    successFunc = function() {
                        pl(messageSel).addClass('successful').get(0).innerText = 'saved';
                        self.campaign.subdomain = subdomain;
                        self.campaign.name = name;
                        self.campaign.description = description;
                        self.campaign.active_from = active_from;
                        self.campaign.active_to = active_to;
                        self.campaign.allowed_languages = allowed_languages;
                        self.campaign.status = status;
                        pl(subdomainSel).get(0).innerText = subdomain;
                        pl(titleSel).get(0).innerText = name;
                        pl(descSel).get(0).innerText = description;
                        pl(activeFromSel).get(0).innerText = self.formattedDateStr(active_from);
                        pl(activeToSel).get(0).innerText = self.formattedDateStr(active_to);
                        pl(langSel).get(0).innerText = self.formattedAllowedLang(allowed_languages);
                        pl(statusSel).get(0).innerText = self.formattedStatus(status);
                        pl(editableSel.join(',')).hide();
                        pl(viewableSel.join(',')).show();
                        if (isAdmin) {
                            pl(adminEditableSel.join(',')).hide();
                            pl(adminViewableSel.join(',')).show();
                        }
                        if (isNew) {
                            pl(newEditableSel.join(',')).hide();
                            pl(newViewableSel.join(',')).show();
                        }
                    },
                    loadFunc = function() {
                        pl(messageSel).addClass('inprogress').get(0).innerText = 'saving...';
                    },
                    errorFunc = function() {
                        pl(messageSel).addClass('errorcolor').get(0).innerText = 'error';
                    },
                    ajax = new AjaxClass('/user/store_campaign/.json', messageId, null,
                        successFunc, loadFunc, errorFunc);

                ajax.setPostData(data);
                ajax.call();

                return false;
            }
        });
    },

    triggerEdit: function() {
        var self = this,
            campaignUniqId = 'campaign_' + self.campaign.campaign_id,
            campaignSel = '#' + campaignUniqId,
            editSel = campaignSel + ' div.campaign-edit-button';
        pl(editSel).get(0).click();
    }

});

function CampaignListClass() {}
pl.implement(CampaignListClass, {

    storeList: function(json) {
        var html = "",
            campaigns = json.user_campaigns,
            isAdmin = json.loggedin_profile.admin,
            pricepoints = new PricepointsClass(json.pricepoints),
            tiles = [],
            campaign,
            tile,
            i;
        if (!campaigns.length || campaigns.length == 0) {
            html = '<div class="boxpanel">'
                +   '<div class="indentedtext">'
                +       '@lang_no_campaigns_found@'
                +   '</div>'
                + '</div>';
            pl('#campaign_list').html(html);
            return;
        }
        for (i = 0; i < campaigns.length; i++) {
            campaign = campaigns[i];
            campaign.pricepoints = pricepoints.pricepointsForId(campaign.campaign_id);
            tile  = new CampaignTileClass();
            tile.store(campaign);
            tiles.push(tile);
        }
        for (i = 0; i < tiles.length; i++) {
            html += tiles[i].makeTile();
        }
        pl('#campaign_list').html(html);
        for (i = 0; i < tiles.length; i++) {
            tiles[i].bindEvents({isAdmin: isAdmin});
        }
    }
});


function ProfileClass() {}
pl.implement(ProfileClass, {
    display: function(json) {
        if (json) {
            this.store(json);
        }
        this.displayFields();
        this.displayPromote();
    },

    store: function(json) {
        if (json) {
            CollectionsClass.prototype.merge(this, json);
        }
    },

    isMine: function() {
        var ismine = false;
        if (this.loggedin_profile) {
            if (this.profile && this.loggedin_profile.profile_id === this.profile.profile_id) {
                ismine = true;
            }
            else if (!this.profile) {
                ismine = true;
            }
        }
        return ismine;
    },

    displayFields: function() {
        var self = this,
            pricepoints = new PricepointsClass(self.pricepoints),
            pricepointsForType = pricepoints.pricepointsForType('INV_REG'),
            pricepointHTML = pricepoints.buttonsHTML(pricepointsForType, 'pricepoint-description-rightbar'),
            profile = this.profile || this.loggedin_profile || {};
 /*        var investor = json.investor ? 'Accredited Investor' : 'Entrepreneur';
            date = new DateClass(),
            joindate = json.joined_date ? date.format(json.joined_date) : 'unknown';
        pl('#profilestatus').html('');
        pl('#title').html(json.title);
        pl('#organization').html(json.organization);
        pl('#phone').html(json.phone || '');
        pl('#address').html(json.address || '');
        pl('#joineddate').html(joindate);
        pl('#investor').html(investor);
        pl('#notifyenabled').html(json.notifyenabled ? 'enabled' : 'disabled');
        pl('#mylistingscount').html(json.posted ? json.posted.length : 0);
        pl('#biddedoncount').html(json.bidon ? json.bidon.length : 0);
*/
        pl('#username').text(profile.username || 'anonymous');
        pl('#email').text(profile.email || '');
        pl('#name').text(profile.name || '');
        if (profile.avatar) {
            pl('#avatar').css('background-image', 'url(' + profile.avatar + ')');
        }
        if (profile.joined_date) {
            pl('#membersince').text('@lang_member_since@ ' + DateClass.prototype.formatDateStr(profile.joined_date));
        }
        if (profile.user_class) {
            pl('#user_class').text(ProfileUserClass.prototype.format(profile.user_class));
        }

        self.handlePaymentButtons(pricepointHTML);
    },

    handlePaymentButtons: function(buttonsHTML) {
        if (buttonsHTML) {
            pl('#pricepoints-wrapper').show();
            if (pl('#pricepoints-wrapper-inner').get(0)) {
                pl('#pricepoints-wrapper-inner').get(0).innerHTML = buttonsHTML;
            }
        }
        else {
            pl('#pricepoints-wrapper').hide();
        }
    },

    /*
     kwota: "0.00"
     value_displayed: null

     out.println("Description: " + pp.getDescription() + "</br>");
     out.println("Amount: " + (pp.getValueDisplayed() == null ? "FREE" : pp.getValueDisplayed()) + "</br>");
     out.println("<form action=\"" + pp.getActionUrl() + "\" method=\"post\" accept-charset=\"utf-8\">"); 
     out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"id\" value=\"" + pp.getSellerId() + "\">");
     out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"kwota\" value=\"" + pp.getAmount() + "\">");
     out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"opis\" value=\"" + pp.getTransactionDescClient() + "\">");
     out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"opis_sprzed\" value=\"" + pp.getTransactionDescSeller() + "\">");
     out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"crc\" value=\"" + pp.getCrc() + "\">");
     out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"pow_url\" value=\"" + pp.getReturnUrlSuccess() + "\">");
     out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"pow_url_blad\" value=\"" + pp.getReturnUrlFailure() + "\">");
     out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"email\" value=\"" + pp.getUserEmail() + "\">");
     out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"nazwisko\" value=\"" + pp.getUserName() + "\">");
     out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"telefon\" value=\"" + pp.getUserPhone() + "\">");
     out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"jezyk\" value=\"" + pp.getPaymentLanguage() + "\">");
     out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"md5sum\" value=\"" + pp.getMd5sum() + "\">");
     out.println("<input type=\"submit\" autocomplete=\"off\" name=\"submit\" value=\"" + pp.getButtonText() + "\">");
     out.println("</form>");
     
        bindApplyDragon: function() {
            pl('#applydragonbutton').unbind().bind('click', function() {
                var complete = function(json) {
                        pl('#applydragonwrapper').hide();
                        pl('#pendingdragonwrapper').show();
                    },

                    ajax = new AjaxClass('/user/request_dragon', 'applydragonmessage', complete);
                pl('#applydragonbutton').hide();
                pl('#applydragonspinner').show();
                ajax.setPost();
                ajax.call();
            });
        },
        

        if (this.loggedin_profile
            && !(this.loggedin_profile.user_class === 'dragon' || this.loggedin_profile.user_class === 'requested_dragon')
            && (!this.profile
                || (this.loggedin_profile && this.loggedin_profile.admin && this.profile.profile_id === this.loggedin_profile.profile_id))) {
            pl('#applydragonwrapper').show();
            this.bindApplyDragon();
        }
        if (this.loggedin_profile
            && this.loggedin_profile.user_class === 'requested_dragon'
            && (!this.profile
                || (this.loggedin_profile && this.loggedin_profile.admin && this.profile.profile_id === this.loggedin_profile.profile_id))) {
            pl('#pendingdragonwrapper').show();
            this.bindApplyDragon();
        }
    },
    */
        
    getUsername: function() {
        var profile = this.profile || this.loggedin_profile || {},
            username = profile.username || 'anonymous';
        return username;
    },

    displayPromote: function() {
        var profile = this.profile || this.loggedin_profile,
            is_loggedin_admin = this.loggedin_profile && this.loggedin_profile.admin,
            not_yet_dragon = !profile.user_class || profile.user_class !== 'dragon',
            promotable = is_loggedin_admin && not_yet_dragon;
        if (promotable) {
            this.bindPromoteButton();
        }
    },

    bindPromoteButton: function() {
        var self = this;
        pl('#promotebox').show();
        pl('#promotebtn').bind({
            click: function() {
                var profile = self.profile || self.loggedin_profile,
                    complete = function() {
                        pl('#promotebtn, #promotecancelbtn').hide();
                        pl('#promotemsg').text('@lang_promoted_reloading@').show();
                        setTimeout(function() {
                            window.location.reload();
                        }, 3000);
                    },

                    url = '/user/promote_to_dragon/' + profile.profile_id,
                    ajax = new AjaxClass(url, 'promotemsg', complete);
                if (pl('#promotemsg').text() && pl('#promotemsg').text().indexOf('Error') !== -1) {
                    pl('#promotebtn').hide();
                }
                else if (pl('#promotecancelbtn').css('display') === 'none') { // first call
                    pl('#promotemsg').text('@lang_promote_this_user@').show();
                    pl('#promotecancelbtn').show();
                }
                else {
                    ajax.setPostData();
                    ajax.call();
                }
                return false;
            }
        });
        pl('#promotecancelbtn').bind({
            click: function() {
                pl('#promotecancelbtn').hide();
                pl('#promotemsg').text('').show();
                pl('#promotebtn').show();
                return false;
            }
        });
    }
});

function ProfilePageClass() {
    var queryString = new QueryStringClass();
    this.passed_id = queryString.vars.id;
    this.json = {};
};

pl.implement(ProfilePageClass,{
    storeListings: function(propertykey, _options) {
        var self = this,
            options = _options || {},

            wrappersel = '#' + propertykey + '_wrapper',
            listings = self.json[propertykey],
            listingfound = false,
            companylist;
        options.propertykey = propertykey;
        options.companydiv = propertykey;
        if (listings && (options.propertyissingle || listings.length > 0)) {
            companylist = new CompanyListClass(options);
            companylist.storeList(self.json);
            pl(wrappersel).show();
            listingfound = true;
        }
        return listingfound;
    },

    storeCampaigns: function() {
        var self = this,
            profile = self.json.loggedin_profile,
            userProfile = self.json.profile,
            canView = profile && (profile.admin || profile.investor),
            canCreate = canView && !userProfile,
            campaigns = self.json.user_campaigns,
            campaignFound = false;
        campaignList = new CampaignListClass();
        if (campaigns && campaigns.length > 0) {
            campaignList.storeList(self.json);
            campaignFound = true;
        }
        if (canView)
            pl('#campaign_list_wrapper').show();
        if (canCreate) {
            pl('#addcampaign a').bind({click: function() {
                var tile = new CampaignTileClass(),
                    html = '';
                tile.makeNew();
                html = tile.makeTile();
                pl('#campaign_add_wrapper').before(html);
                tile.bindEvents({isAdmin: profile.admin});
                tile.triggerEdit();
                return false;
            }});
            pl('#campaign_add_wrapper').show();
        }
        return campaignFound;
    },

    loadPage: function() {
        var self = this,
            completeFunc = function(json) {
                var header = new HeaderClass(),
                    profile = new ProfileClass(),
                    //notifyList = new NotifyListClass(),
                    listprops = [ 'edited_listing',
                        'active_listings',
                        'admin_posted_listings',
                        'admin_frozen_listings',
                        'monitored_listings',
                        'withdrawn_listings',
                        'frozen_listings'
                    ],
                    seeallurl = '/profile-listing-page.html?' + (self.passed_id ? 'id=' + self.passed_id + '&' : '') + 'type=',
                    options = {
                        edited_listing: { propertyissingle: true, fullWidth: true },

                        active_listings: { seeall: seeallurl + 'active', fullWidth: true },

                        monitored_listings: { seeall: seeallurl + 'monitored', fullWidth: true },

                        withdrawn_listings: { seeall: seeallurl + 'withdrawn', fullWidth: true },

                        frozen_listings: { seeall: seeallurl + 'frozen', fullWidth: true },

                        admin_posted_listings: { seeall: seeallurl + 'admin_posted', fullWidth: true },

                        admin_frozen_listings: { seeall: seeallurl + 'admin_frozen', fullWidth: true }
                    },

                    listingfound = false,
                    propertykey,
                    i;
                self.json = json;
                header.setLogin(json);
                profile.display(json);
                //notifyList.display(json);
                for (i = 0; i < listprops.length; i++) {
                    propertykey = listprops[i];
                    if (self.storeListings(propertykey, options[propertykey])) {
                        listingfound = true;
                    }
                }
                self.storeCampaigns();
                if (!listingfound) {
                    pl('#no_listings_wrapper').show();
                }
                if (profile.isMine()) {
                    pl('#editprofilebutton').show();
                    pl('.titleuser').text('');
                }
                else {
                    var userText = '@lang_for_user@';
                    userText = userText.replace('%1$s', profile.getUsername())
                    pl('.titleusername').text(userText);
                    pl('.titleyour').text('');
                    pl('#encourageuser').hide();
                }
                pl('.preloader').hide();
                pl('.wrapper').show();
            },

            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader, .companyheader').hide();
                pl('.errorwrapper').show();
            },

            url = this.passed_id ? '/user/get/' + this.passed_id : '/listings/discover_user',
            ajax = new AjaxClass(url, 'profilemsg', completeFunc, null, null, error);
        ajax.call();
    }
});

function ProfileListingPageClass() {
    this.json = {};
    this.queryString = new QueryStringClass();
    this.passed_id = this.queryString.vars.id;
    this.type = this.queryString.vars.type || 'active';
    this.data = { max_results: 20 };
    this.urlmap = {
        active: '/listings/user/active',
        monitored: '/listings/monitored',
        withdrawn: '/listings/user/withdrawn',
        frozen: '/listings/user/frozen',
        admin_posted: '/listings/posted',
        admin_frozen: '/listings/frozen'
    };
    this.urlroot = this.urlmap[this.type] || this.urlmap['active'];
    this.url = this.passed_id ? this.urlroot + '/' + this.passed_id : this.urlroot;
}

pl.implement(ProfileListingPageClass, {
    loadPage: function() {
        var self = this,
            completeFunc = function(json) {
                var header = new HeaderClass(),
                    profile = new ProfileClass(),
                    companyList = new CompanyListClass({ fullWidth: true });
                self.json = json;
                header.setLogin(json);
                profile.display(json);
                companyList.storeList(json);
                pl('.titletype').text(self.type === 'monitored' ? '@lang_watched@' : self.type.toUpperCase());

                //if (!listingfound) {
                //    pl('#no_listings_wrapper').show();
                //}
                if (profile.isMine()) {
                    pl('#editprofilebutton').show();
                    pl('.titleuser').text('');
                }
                else {
                    var userText = '@lang_for_user@';
                    userText = userText.replace('%1$s', profile.getUsername())
                    pl('.titleusername').text(userText);
                    pl('.titleyour').text('');
                    pl('#encourageuser').hide();
                }

                pl('.preloader').hide();
                pl('.wrapper').show();
            },

            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader, .companyheader').hide();
                pl('.errorwrapper').show();
            },

            ajax = new AjaxClass(self.url, 'companydiv', completeFunc, null, null, error);
        ajax.setGetData(this.data);
        ajax.call();
    }
});

function EditProfileClass() {}
pl.implement(EditProfileClass, {
    getUpdater: function() {
        var self = this;
        return function(newdata, loadFunc, errorFunc, successFunc) {
            var old_notify_enabled = pl('#notify_enabled').hasClass('checkboxcheckedicon') ? true : false,
                notify_enabled = !old_notify_enabled,
                data = {
                    profile: {
                        name: pl('#name').attr('value'),
                        nickname: pl('#username').attr('value'),
                        notify_enabled: notify_enabled
                        /*
                        profile_id: self.profile_id,
                        status: self.status,
                        open_id: self.open_id,
                        investor: pl('#investor').attr('value') ? 'true' : 'false'
                        title: pl('#title').attr('value'),
                        organization: pl('#organization').attr('value'),
                        facebook:'',
                        twitter:'',
                        linkedin:''
                        */
                    }
                },

                ajax = new AjaxClass('/user/autosave', '', null, successFunc, loadFunc, errorFunc),
                field;
            for (field in newdata) {
                data.profile[field] = newdata[field];
            }
            ajax.setPostData(data);
            ajax.call();
        };
    },

    displayDeactivate: function() {
        var self = this,
            deactivateable = true; // always returned by user/loggedin
        if (deactivateable) {
            pl('#deactivateguardlink').bind('click', function() {
                pl('#deactivateguard').show();
                self.bindDeactivateButton();
            });
        }
    },

    bindDeactivateButton: function() {
        var self = this;
        pl('#deactivatebox').show();
        pl('#deactivatebtn').bind({
            click: function() {
                var completeFunc = function() {
                        pl('#deactivatemsg').addClass('successful').html('@lang_deactivated_logout@');
                        pl('#deactivatebtn, #deactivatecancelbtn').hide();
                        setTimeout(function() {
                            window.location = pl('#logoutlink').attr('href');
                        }, 3000);
                    },

                    url = '/user/deactivate/' + self.profile_id,
                    ajax = new AjaxClass(url, 'deactivatemsg', completeFunc);
                if (pl('#deactivatecancelbtn').css('display') === 'none') { // first call
                    pl('#deactivatemsg, #deactivatecancelbtn').show();
                }
                else {
                    ajax.setPost();
                    ajax.call();
                }
                return false;
            }
        });
        pl('#deactivatecancelbtn').bind({
            click: function() {
                pl('#deactivatemsg, #deactivatecancelbtn').hide();
                return false;
            }
        });
    },

    display: function(discoverjson) {
        this.store(discoverjson);
    },

    store: function(discoverjson) {
        //properties = ['profile_id', 'status', 'name', 'username', 'open_id', 'profilestatus', 'title', 'organization', 'email', 'phone', 'address'];
        var self = this,
            json = discoverjson && discoverjson.loggedin_profile || {},

            properties = ['profile_id', 'username', 'email', 'name'],
            textFields = ['username', 'name'],
            i, property, textFields, textFieldId, textFieldObj, notifyCheckbox; 
        self.profile_id = json.profile_id;
        self.admin = json.admin;
        self.updateUrl = '/user/update?id=' + self.profile_id;
        for (i = 0; i < properties.length; i++) {
            property = properties[i];
            self[property] = json[property];
        }
        for (i = 0; i < textFields.length; i++) {
            textFieldId = textFields[i];
            textFieldObj = new TextFieldClass(textFieldId, json[textFieldId], self.getUpdater(), 'personalinfomsg');
            textFieldObj.fieldBase.addValidator(textFieldObj.fieldBase.validator.isNotEmpty);
/*
            if (textFieldId === 'email') {
                textFieldObj.fieldBase.addValidator(textFieldObj.fieldBase.validator.isEmail);
            }
*/
            if (textFieldId === 'username') {
                textFieldObj.fieldBase.addValidator(function(username) {
                    var successFunc = function(json) {
                            var icon = new ValidIconClass('usernameicon');
                            if (json) {
                                icon.showValid();
                                pl('#personalinfomsg').text('');
                            }
                            else {
                                icon.showInvalid();
                                pl('#personalinfomsg').html('<span class="attention">@lang_nickname_taken@</span>');
                            } 
                        },

                        icon = new ValidIconClass('usernameicon'),
                        ajax;
                    if (!username || !username.length) {
                        return '@lang_nickname_empty@';
                    }
                    else if (username.length < 3) {
                        return '@lang_nickname_too_short@';
                    }
                    else if (username.length > 30) {
                        return '@lang_nickname_too_long@';
                    }
                    else {
                        ajax = new AjaxClass('/user/check_user_name', 'personalinfomsg', null, successFunc);
                        ajax.setGetData({ name: username });
                        ajax.call();
                    }
                    return 0;
                });
                textFieldObj.fieldBase.postSuccessFunc = function(newval) {
                    pl('#headerusername').text(newval);
                };
            }
            if (textFieldId === 'name') {
                textFieldObj.fieldBase.addValidator(textFieldObj.fieldBase.validator.makeLengthChecker(3, 100));
            }
            textFieldObj.bindEvents();
        }
        notifyCheckbox = new CheckboxFieldClass('notify_enabled', json.notify_enabled, self.getUpdater(), 'personalinfomsg');
        notifyCheckbox.bindEvents();
/*

        newPassword = new TextFieldClass('newpassword', '', function(){},
 'passwordmsg');
        passwordOptions = {
            length: [8, 32],
            badWords: ['password', self.name, self.username, self.email, (self.email&&self.email.indexOf('@')>0?self.email.split('@')[0]:'')],
            badSequenceLength: 3
        };

        investorCheckbox = new CheckboxFieldClass('investor', json.investor, self.getUpdater(), 'personalinfomsg');
        investorCheckbox.bindEvents();

        newPassword.fieldBase.addValidator(newPassword.fieldBase.validator.makePasswordChecker(passwordOptions));
        newPassword.fieldBase.validator.postValidator = function(result) {
            if (result === 0) {
                pl('#confirmpassword').removeAttr('disabled');
            }
            else {
                pl('#confirmpassword').attr({disabled: 'disabled'});
            }
        };
        newPassword.bindEvents();
        confirmPassword = new TextFieldClass('confirmpassword', '', self.getUpdater(), 'passwordmsg');
        confirmPassword.fieldBase.addValidator(function(val) {
            if (pl('#newpassword').attr('value') === val) {
                return 0;
            }
            else {
                return "confirm must match new password";
            }
        });
        confirmPassword.bindEvents();
 */
        pl('#email').text(json.email || '');
        this.bindInfoButtons();
        self.displayDeactivate();
        pl('#personalcolumn').show();
    },

    hideAllInfo: function() {
        pl('.sideinfo').removeClass('sideinfodisplay');
    },

    bindInfoButtons: function() {
        var self = this;
        pl('input.text, select.text, textarea.inputwidetext').bind({
            focus: function(e) {
                var evt = new EventClass(e),
                    infoel = evt.target().parentNode.nextSibling.nextSibling;
                self.hideAllInfo();
                pl(infoel).addClass('sideinfodisplay');
            },

            blur: self.hideAllInfo
        });
        pl('.sideinfo').bind('click', self.hideAllInfo);
    },

    load: function() {
        var self = this,
            completeFunc = function(json) {
                (new HeaderClass()).setLogin(json);
                self.display(json);
                pl('#personalinfomsg').text('');
                pl('.preloader').hide();
                pl('.wrapper').show();
            },

            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader, .companyheader').hide();
                pl('.errorwrapper').show();
            },

            ajax = new AjaxClass('/listings/discover_user', 'personalinfomsg', completeFunc, null, null, error);
        ajax.call();
    }
});

function ProfileListClass() {
    this.queryString = new QueryStringClass();
    this.type = this.queryString.vars.type || 'all';
}

pl.implement(ProfileListClass, {
    store: function(json) {
        if (json) {
            CollectionsClass.prototype.merge(this, json);
        }
    },
   
    display: function(json) {
        if (json) {
            this.store(json);
        }
        if (this.type === 'listers') {
            pl('#typetitle').text('@lang_entrepreneuers@');
            pl('#listersubtitle').show();
        }
        else if (this.type === 'dragons') {
            pl('#typetitle').text('@lang_investors@');
            pl('#dragonsubtitle').show();
        }
        else {
            pl('#typetitle').text('@lang_users@');
        }
        this.displayList(json.users);
    },

    displayEmptyList: function() {
        var listhtml = '\
            <div class="messageline">\
                <p class="messagetext"><i>@lang_no_results@</i></p>\
            </div>\
            ';
        pl('#profilelistcontainer').removeClass('addlistingcontainerfilled');
        pl('#profilelist').html(listhtml);
    },

    displayList: function(results) {
        var list = results || [],
            more_results_url = this.users_props && this.users_props.more_results_url,
            listhtml = this.makeList(list);
        if (listhtml) {
            pl('#profilelistcontainer').addClass('addlistingcontainerfilled');
            pl('#profilelist').html(listhtml);
        }
        else {
            this.displayEmptyList();
        }
        if (more_results_url) {
            pl('#profilelist').after('<div class="showmore profilelistshowmore hoverlink" id="moreresults"><span class="initialhidden" id="moreresultsurl">'
                + more_results_url + '</span><span id="moreresultsmsg">@lang_more@</span></div>\n');
            this.bindMoreResults();
        }
    },

    makeList: function(list) {
        var listhtml = '',
            listitem,
            i;
        for (i = 0; i < list.length; i++) {
            listitem = list[i];
            if (!listitem.status || listitem.status !== 'disabled' || this.loggedin_profile && this.loggedin_profile.admin) {
                listhtml += this.makeListItem(listitem);
            }
        }
        return listhtml;
    },

    makeListItem: function(listitem) {
        var url = '/profile-page.html?id=' + listitem.profile_id,
            avatarstyle = listitem.avatar
                ? ' style="background-image: url(' + listitem.avatar + ')"'
                : '',
            emailtext = listitem.email
                ? '<span class="profilelistemail">' + listitem.email+ '</span>'
                : '',
           	statustext =  listitem.status && listitem.status !== 'active'
                ? '<span class="profileliststatus">@lang_inactive@</span>'
                : '',
			admintext =  listitem.admin ? '<span class="profilelistadmin">@lang_admin@</span>' : '',
			userclasstext =  listitem.user_class && this.type !== 'dragons'
                ? '<span class="profilelistuserclass">' + ProfileUserClass.prototype.format(listitem.user_class) + '</span>'
                : '',
            nametext = listitem.name
                ? '@lang_name@:<span class="profilelistlastlogin">'
                    + SafeStringClass.prototype.htmlEntities(listitem.name) + '</span></br>'
                : '',
            locationtext = listitem.location
                ? '@lang_location@:<span class="profilelistlastlogin">'
                    + SafeStringClass.prototype.htmlEntities(listitem.location) + '</span></br>'
                : '',
            lastlogintext = listitem.last_login
                ? '@lang_last_login@:<span class="profilelistlastlogin">'
                    + DateClass.prototype.format(listitem.last_login) + '</span></br>'
                : '',
			pendingtext =  listitem.edited_listing
                ? '<span class="profilelistpending">@lang_has_pending@</span><br/>'
                : '',
            html = '\
            <div class="messageline">\
                <a href="' + url + '">\
                <div class="profilelistavatar"' + avatarstyle + '></div>\
                <p class="messagetext profilelistheader">\
			            <span class="profilelistusername">' + listitem.username + '</span>\
			        ' + emailtext + '\
                    ' + statustext + '\
                    ' + userclasstext + '\
                    ' + admintext + '\
                </p>\
                <p class="messagetext profilelistdetails">\
                    ' + nametext + '\
                    ' + locationtext + '\
                    ' + lastlogintext + '\
                    ' + pendingtext + '\
                </p>\
                </a>\
            </div>\
            ';
        return html;
    },

    bindMoreResults: function() {
        var self = this;
        pl('#moreresults').bind('click', function() {
            var completeFunc = function(json) {
                    var users = json.users || [],
                        more_results_url = users.length > 0 && json.users_props && json.users_props.more_results_url,
                        listhtml = self.makeList(users);
                    if (listhtml) {
                        pl('#profilelist div:last-child').after(listhtml);
                    }
                    if (more_results_url) {
                        pl('#moreresultsurl').text(more_results_url);
                        pl('#moreresultsmsg').text('@lang_more@');
                    }
                    else {
                        pl('#moreresultsmsg').text('');
                        pl('#moreresults').removeClass('hoverlink').unbind();
                    }
                },

                more_results_url = pl('#moreresultsurl').text(),
                index = more_results_url ? more_results_url.indexOf('?') : -1,
                components = more_results_url && index >= 0 ? [ more_results_url.slice(0, index), more_results_url.slice(index+1) ] : [ more_results_url, null ],
                url = components[0],
                parameters = components[1] ? components[1].split('&') : null,
                ajax,
                data,
                keyval,
                i;
            if (more_results_url) {
                ajax = new AjaxClass(url, 'moreresultsmsg', completeFunc);
                if (parameters) {
                    data = {};
                    for (i = 0; i < parameters.length; i++) {
                        keyval = parameters[i].split('=');
                        data[keyval[0]] = keyval[1];
                    }
                    ajax.setGetData(data);
                }
                ajax.call();
            }
            else {
                pl('#moreresultsmsg').text('');
                pl('#moreresults').removeClass('hoverlink').unbind();
            }
        });
    },

    loadPage: function() {
        var self = this,
            complete = function(json) {
                (new HeaderClass()).setLogin(json);
                self.display(json);
                pl('#profilelistmsg').text('');
                pl('.preloader').hide();
                pl('.wrapper').show();
            },

            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader, .companyheader').hide();
                pl('.errorwrapper').show();
            },

            url = '/user/' + this.type + '?max_results=20',
            ajax = new AjaxClass(url, 'profilelistmsg', complete, null, null, error);
        ajax.call();
    }

});

