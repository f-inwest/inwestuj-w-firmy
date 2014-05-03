function ListingClass() {
    var queryString = new QueryStringClass();
    this.id = queryString.vars.id;
    this.listing_id = this.id;
    this.preview = queryString.vars.preview;
    this.imagepanel = new ImagePanelClass();
};
pl.implement(ListingClass, {
    store: function(json) {
        var key;
        if (json && json.listing && json.listing.listing_id) {
            for (key in json.listing) {
                this[key] = json.listing[key];
            }
        }
        this.login_url = json && json.login_url;
        this.loggedin_profile = json && json.loggedin_profile;
        this.loggedin_profile_id = this.loggedin_profile && this.loggedin_profile.profile_id;
        this.listing_url = 'https://inwestujwfirmy.pl/company-page.html?id=' + this.listing_id;
        this.listing_public_title = 'Inwestuj w Firmy: ' + this.title;
        this.pricepoints = json.pricepoints;
        //console.log('pricepoints:', this.pricepoints);
        if (this.preview) {
            pl('#header').hide();
            pl('#footer').hide();
        }
     },

    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('basics');
                if (self.preview) {
                    pl('#header').hide();
                    pl('#footer').hide();
                }
                else {
                    header.setLogin(json);
                }
                companybanner.display(json);
                self.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader, .companyheader').hide();
                pl('.errorwrapper').show();
            },
            ajax = new AjaxClass('/listings/get/' + this.listing_id, 'listingstatus', complete, null, null, error);
        ajax.call();
    },

    display: function(json) {
        if (json) {
            this.store(json);
        }
        this.displayBasics();
        this.displayInvest();
        this.displayGoto();
        this.displayMap();
        this.displayDocuments();
        this.displayFunding();
        this.displaySocial();
        this.displayDelete();
        this.displayWithdraw();
        this.displayApprove();
        this.displaySendback();
        this.displayFreeze();
        this.displayPricepoints();
        if (this.preview) {
            pl('#previewoverlay').show();
        }
        pl('#basicswrapper').show();
    },

    displayBasics: function() {
        var html = this.summary ? HTMLMarkup.prototype.stylize(this.summary) : '@lang_listing_summary_empty@';
        this.displayPics();
        this.displayVideo();
        pl('#summary').html(html);
    },

    displayPics: function() {
        this.imagepanel.setListing(this).display();
    },

    displayVideo: function() {
        var url = this.video,
            secureUrl = url && url.indexOf('http:') == 0 ? url.replace('http:', 'https:') : url;
        if (secureUrl) {
            pl('#videolink').attr({href: secureUrl});
            pl('#videopresentation').attr({src: secureUrl});
            pl('#videowrapper').show();
        }
    },

    displayInvest: function() {
        var page = this.loggedin_profile
                ? (this.loggedin_profile.profile_id === this.profile_id
                    ? 'company-owner-bids-page.html'
                    : 'company-investor-bids-page.html')
                : 'company-bids-page.html',
            url = '/' + page + '?id=' + this.listing_id;
        if (this.status === 'active' && this.asked_fund) {
            pl('#investbutton').attr('href', url).show();
        }
        else {
            pl('#investbutton').hide();
        }
    },

    displayGoto: function() {
        this.displayBasicsButton();
        this.displayValuationButton();
        this.displayCashFlowButton();
        this.displayModelButton();
        this.displayPresentationButton();
        this.displayAddDocumentButton();
        this.displayRequestDocumentButtons();
    },

    displayBasicsButton: function() {
        var self = this,
            text,
            ajax,
            url;
        if (this.status === 'new') {
            text = '@lang_basics@';
            url = '/new-listing-basics-page.html';
        }
        else if ((this.status === 'posted' || this.status === 'active')
                && this.loggedin_profile && this.loggedin_profile.profile_id === this.profile_id) {
            text = '@lang_revise_info@';
            url = '/active-listing-basics-page.html?id=' + this.listing_id;
        }
        if (url) {
            pl('#basicsbutton').text(text);
            pl('#basicsbutton').unbind('click').bind('click', function(e) {
                document.location = url;
                e.preventDefault();
            });
            pl('#basicsbutton').show();
        }
    },

    displayValuationButton: function() {
        var self = this,
            hasValuation = MicroListingClass.prototype.getHasValuation(this),
            text,
            ajax,
            url;
        if (this.status === 'new') {
            if (hasValuation) {
                text = '@lang_valuation@';
            }
            else {
                text = '@lang_valuation@';
            }
            url = '/new-listing-valuation-page.html';
        }
        else if (this.status === 'posted' || this.status === 'active') {
            if (hasValuation) {
                if (this.loggedin_profile && this.loggedin_profile.profile_id === this.profile_id) {
                    text = '@lang_valuation@';
                    url = '/active-listing-valuation-page.html?id=' + this.listing_id;
                }
                else {
                    text = '@lang_valuation@';
                    url = '/company-valuation-page.html?id=' + this.listing_id;
                }
            }
            else {
                if (this.loggedin_profile) {
                    if (this.loggedin_profile.profile_id === this.profile_id) {
                        text = '@lang_valuation@';
                        url = '/active-listing-valuation-page.html?id=' + this.listing_id;
                    }
                    else {
                        text = '@lang_request_valuation@';
                        ajax = new AjaxClass('/listing/ask_owner', 'valuationbutton', function() {
                            document.location = '/company-questions-page.html?id=' + self.listing_id;
                        });
                        ajax.setPostData({
                            message: {
                                listing_id: this.listing_id,
                                text: '@lang_valuation_request_message@'
                            }
                        })
                    }
                }
                else {
                    if (this.login_url) {
                        text = '@lang_sign_in_to_request_valuation@';
                        url = '/login-page.html?url=' + encodeURIComponent('/company-page.html?id=' + this.listing_id);
                    }
                    else {
                        text = '@lang_no_valuation@';
                    }
                }
            }
        }
        else {
            text = '@lang_no_valuation@';
        }
        pl('#valuationbutton').text(text);
        if (ajax) {
            pl('#valuationbutton').bind('click', function() {
                pl('#valuationbutton').unbind();
                ajax.call();
            });
        }
        else if (url) {
            pl('#valuationbutton').bind('click', function() {
                document.location = url;
            });
        }
    },

    displayCashFlowButton: function() {
        var self = this,
            hasCashFlow = MicroListingClass.prototype.getHasCashFlow(this),
            text,
            ajax,
            url;
        if (this.status === 'new') {
            if (hasCashFlow) {
                text = '@lang_cashflow@';
            }
            else {
                text = '@lang_cashflow@';
            }
            url = '/new-listing-cashflow-page.html';
        }
        else if (this.status === 'posted' || this.status === 'active') {
            if (hasCashFlow) {
                if (this.loggedin_profile && this.loggedin_profile.profile_id === this.profile_id) {
                    text = '@lang_cashflow@';
                    url = '/active-listing-cashflow-page.html?id=' + this.listing_id;
                }
                else {
                    text = '@lang_cashflow@';
                    url = '/company-cashflow-page.html?id=' + this.listing_id;
                }
            }
            else {
                if (this.loggedin_profile) {
                    if (this.loggedin_profile.profile_id === this.profile_id) {
                        text = '@lang_cashflow@';
                        url = '/active-listing-cashflow-page.html?id=' + this.listing_id;
                    }
                    else {
                        text = '@lang_request_cashflow@';
                        ajax = new AjaxClass('/listing/ask_owner', 'cashflowbutton', function() {
                            document.location = '/company-questions-page.html?id=' + self.listing_id;
                        });
                        ajax.setPostData({
                            message: {
                                listing_id: this.listing_id,
                                text: '@lang_cashflow_request_message@'
                            }
                        })
                    }
                }
                else {
                    if (this.login_url) {
                        text = '@lang_sign_in_to_request_cashflow@';
                        url = '/login-page.html?url=' + encodeURIComponent('/company-page.html?id=' + this.listing_id);
                    }
                    else {
                        text = '@lang_no_cashflow@';
                    }
                }
            }
        }
        else {
            text = '@lang_no_cashflow@';
        }
        pl('#cashflowbutton').text(text);
        if (ajax) {
            pl('#cashflowbutton').bind('click', function() {
                pl('#cashflowbutton').unbind();
                ajax.call();
            });
        }
        else if (url) {
            pl('#cashflowbutton').bind('click', function() {
                document.location = url;
            });
        }
    },

    displayModelButton: function() {
        var self = this,
            hasBmc = MicroListingClass.prototype.getHasBmc(this),
            text,
            ajax,
            url;
        if (this.status === 'new') {
            if (hasBmc) {
                text = '@lang_model@';
            }
            else {
                text = '@lang_model@';
            }
            url = '/new-listing-bmc-page.html';
        }
        else if (this.status === 'posted' || this.status === 'active') {
            if (hasBmc) {
                if (this.loggedin_profile && this.loggedin_profile.profile_id === this.profile_id) {
                    text = '@lang_model@';
                    url = '/active-listing-bmc-page.html?id=' + this.listing_id;
                }
                else {
                    text = '@lang_model@';
                    url = '/company-model-page.html?id=' + this.listing_id;
                }
            }
            else {
                if (this.loggedin_profile) {
                    if (this.loggedin_profile.profile_id === this.profile_id) {
                        text = '@lang_model@';
                        url = '/active-listing-bmc-page.html?id=' + this.listing_id;
                    }
                    else {
                        text = '@lang_request_model@';
                        ajax = new AjaxClass('/listing/ask_owner', 'modelbutton', function() {
                            document.location = '/company-questions-page.html?id=' + self.listing_id;
                        });
                        ajax.setPostData({
                            message: {
                                listing_id: this.listing_id,
                                text: '@lang_model_request_message@'
                            }
                        })
                    }
                }
                else {
                    if (this.login_url) {
                        text = '@lang_sign_in_to_request_model@';
                        url = '/login-page.html?url=' + encodeURIComponent('/company-page.html?id=' + this.listing_id);
                    }
                    else {
                        text = '@lang_no_model@';
                    }
                }
            }
        }
        else {
            text = '@lang_no_model@';
        }
        pl('#modelbutton').text(text);
        if (ajax) {
            pl('#modelbutton').bind('click', function() {
                pl('#modelbutton').unbind();
                ajax.call();
            });
        }
        else if (url) {
            pl('#modelbutton').bind('click', function() {
                document.location = url;
            });
        }
    },

    displayPresentationButton: function() {
        var self = this,
            hasIp = MicroListingClass.prototype.getHasIp(this),
            text,
            ajax,
            url;
        if (this.status === 'new') {
            if (hasIp) {
                text = '@lang_presentation@';
            }
            else {
                text = '@lang_presentation@';
            }
            url = '/new-listing-qa-page.html';
        }
        else if (this.status === 'posted' || this.status === 'active') {
            if (hasIp) {
                if (this.loggedin_profile && this.loggedin_profile.profile_id === this.profile_id) {
                    text = '@lang_presentation@';
                    url = '/active-listing-qa-page.html?id=' + this.listing_id;
                }
                else {
                    text = '@lang_presentation@';
                    url = '/company-slides-page.html?id=' + this.listing_id;
                }
            }
            else {
                if (this.loggedin_profile) {
                    if (this.loggedin_profile.profile_id === this.profile_id) {
                        text = '@lang_presentation@';
                        url = '/active-listing-qa-page.html?id=' + this.listing_id;
                    }
                    else {
                        text = '@lang_request_presentation@';
                        ajax = new AjaxClass('/listing/ask_owner', 'modelbutton', function() {
                            document.location = '/company-questions-page.html?id=' + self.listing_id;
                        });
                        ajax.setPostData({
                            message: {
                                listing_id: this.listing_id,
                                text: '@lang_presentation_request_message@'
                            }
                        })
                    }
                }
                else {
                    if (this.login_url) {
                        text = '@lang_sign_in_to_request_presentation@';
                        url = '/login-page.html?url=' + encodeURIComponent('/company-page.html?id=' + this.listing_id);
                    }
                    else {
                        text = '@lang_no_presentation@';
                    }
                }
            }
        }
        else {
            text = '@lang_no_presentation@';
        }
        pl('#presentationbutton').text(text);
        if (ajax) {
            pl('#presentationbutton').bind('click', function() {
                pl('#presentationbutton').unbind();
                ajax.call();
            });
        }
        else if (url) {
            pl('#presentationbutton').bind('click', function() {
                document.location = url;
            });
        }
    },

    displayAddDocumentButton: function() {
        var url;
        if (this.loggedin_profile && this.loggedin_profile.profile_id === this.profile_id) {
            if (this.status === 'new') {
                url = '/new-listing-documents-page.html';
            }
            else if (this.status === 'posted' || this.status === 'active') {
                url = '/active-listing-documents-page.html?id=' + this.listing_id;
            }
            if (url) {
                pl('#adddocumentbutton').bind('click', function() {
                    document.location = url;
                }).show();
            }
        }
    },

    displayRequestDocumentButtons: function() {
        var self = this;
        if (this.loggedin_profile && this.loggedin_profile.profile_id !== this.profile_id && this.status === 'active' && !this.presentation_id) {
            pl('#requestpresentationbutton').bind('click', function() {
                var ajax = new AjaxClass('/listing/ask_owner', 'requestpresentationbutton', function() {
                        document.location = '/company-questions-page.html?id=' + self.listing_id;
                    });
                ajax.setPostData({
                    message: {
                        listing_id: self.listing_id,
                        text: '@lang_request_presentation_document@'
                    }
                });
                pl('#requestpresentationbutton').unbind();
                ajax.call();
            }).show();
        }
        if (this.loggedin_profile && this.loggedin_profile.profile_id !== this.profile_id && this.status === 'active' && !this.business_plan_id) {
            pl('#requestbusinessplanbutton').bind('click', function() {
                var ajax = new AjaxClass('/listing/ask_owner', 'requestbusinessplanbutton', function() {
                        document.location = '/company-questions-page.html?id=' + self.listing_id;
                    });
                ajax.setPostData({
                    message: {
                        listing_id: self.listing_id,
                        text: '@lang_request_plan_document@'
                    }
                });
                pl('#requestbusinessplanbutton').unbind();
                ajax.call();
            }).show();
        }
        if (this.loggedin_profile && this.loggedin_profile.profile_id !== this.profile_id && this.status === 'active' && !this.financials_id) {
            pl('#requestfinancialsbutton').bind('click', function() {
                var ajax = new AjaxClass('/listing/ask_owner', 'requestfinancialsbutton', function() {
                        document.location = '/company-questions-page.html?id=' + self.listing_id;
                    });
                ajax.setPostData({
                    message: {
                        listing_id: self.listing_id,
                        text: '@lang_request_statements_document@'
                    }
                });
                pl('#requestfinancialsbutton').unbind();
                ajax.call();
            }).show();
        }
    },

    displayMap: function() {
        this.address = this.address || '@lang_unknown_address@';
        //this.addressurl = this.addressurl || 'https://nominatim.openstreetmap.org/search?q=' + encodeURIComponent(this.address);
        this.addressurl = 'https://maps.google.com/maps?q='+this.latitude+','+this.longitude+'+('+encodeURIComponent(this.title)+', '+encodeURIComponent(this.address)+')';
        this.latitude = this.latitude || '50.109619';
        this.longitude = this.longitude || '18.5492673';
        //this.mapurl = 'http://ojw.dev.openstreetmap.org/StaticMap/?lat=' + this.latitude + '&lon=' + this.longitude + '&z=5&show=1&fmt=png&w=302&h=302&att=none';
        this.mapurl = 'https://maps.googleapis.com/maps/api/staticmap?center=' + this.latitude + ',' + this.longitude + '&zoom=7&size=302x302&maptype=roadmap&markers=color:blue%7Clabel:' + encodeURI(this.title) + '%7C' + encodeURI(this.address) + '&style=feature:landscape|visibility:on|color:0xffffff|saturation:-100|lightness:4&style=feature:road.highway|visibility:on|color:0x4cc7df|saturation:100|lightness:-7&style=feature:road.arterial|visibility:on|color:0x4cc7df|saturation:-30|lightness:-3&style=feature:road.local|color:0xffffff|saturation:-30|lightness:-3&style=feature:landscape.natural|color:0xffffff|saturation:-30|lightness:-3&style=feature:poi.park|color:0xffffff|saturation:-30|lightness:-3&sensor=false';
        pl('#fulladdress').html(this.address || '@lang_unknown_address@');
        pl('#addresslink').attr({href: this.addressurl});
        pl('#mapimg').attr({src: this.mapurl});
    },

    displayDocumentLink: function(type, docId) {
        var linkId = type + 'link',
            wrapperId = type + 'wrapper',
            url;
        if (docId) {
            url = '/file/download/' + docId;
            pl('#'+linkId).attr({href: url});
            pl('#'+wrapperId).show();
        }
    },

    displayDocuments: function() {
        if (this.presentation_id || this.business_plan_id || this.financials_id) {
            this.displayDocumentLink('presentation', this.presentation_id);
            this.displayDocumentLink('businessplan', this.business_plan_id);
            this.displayDocumentLink('financials', this.financials_id);
            pl('#documentboxwrapper').show();
        }
    },

    displayFunding: function() {
//        var total_raised = this.total_raised && this.total_raised > 0 ? CurrencyClass.prototype.format(this.total_raised) : '$0';
        if (this.asked_fund) {
            pl('#suggested_amt').text(CurrencyClass.prototype.format(this.suggested_amt, this.currency));
            pl('#suggested_pct').text(this.suggested_pct);
            pl('#suggested_val').text(CurrencyClass.prototype.format(this.suggested_val, this.currency));
//            pl('#total_raised').text(total_raised);
/*
            if (this.num_bids && this.num_bids > 0) {
                this.best_bid_pct = 10 + 5*Math.floor(8*Math.random()); // FIXME
                this.best_bid_amt = this.valuation ? Math.floor((this.best_bid_pct / 100) * this.valuation) : 1000 + 1000*Math.floor(99*Math.random());
                this.best_bid_val = this.valuation || (100 * this.best_bid_amt / this.best_bid_pct); // FIXME
                pl('#num_bids').html(this.num_bids);
                pl('#best_bid_amt').html(CurrencyClass.prototype.format(this.best_bid_amt));
                pl('#best_bid_pct').html(this.best_bid_pct);
                pl('#best_bid_val').html(CurrencyClass.prototype.format(this.best_bid_val));
                pl('#bidboxinfo').show();
            }
            else {
                pl('#bidboxstatus').html('NO BIDS').show();
            }
            pl('#bidboxtitle').show();
            pl('#bidbox').show();
*/
            pl('#suggestedinfo').show();
            pl('#fundingbutton').text('@lang_revise_funding@');
        }
        else {
            pl('#suggestedmsg').html('@lang_not_seeking_funding@').show();
            pl('#fundingbutton').text('@lang_ask_for_funding@');
        }
        if (this.status === 'new' || this.status === 'posted') {
            if (this.asked_fund) {
                pl('#fundingbutton').text('@lang_revise_funding@').show();
            }
            else {
                pl('#fundingbutton').text('@lang_ask_for_funding@').show();
            }
        }
    },

    displaySocial: function() {
        if (this.preview || this.status !== 'active') { // their iframe usage busts during preview
            pl('#socialsidebox').html('<p>@lang_social_display_message@</p>');
        }
        else {
            this.displayTwitter();
            this.displayFacebook();
            this.displayGooglePlus();
        }
    },

    displayTwitter: function() {
        var twitterurl = 'https://twitter.com/share?url=' + encodeURIComponent(this.listing_url) + '&text=' + encodeURIComponent(this.listing_public_title);
        pl('#twitterbanner a').attr({
            href: twitterurl,
            'data-url': this.listing_url,
            'data-text': this.listing_public_title
        });
        !function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");
    },

    displayFacebook: function() {
        this.addMetaTags('property', {
            'og:title': this.listing_public_title,
            'og:type': 'company',
            'og:url': this.listing_url,
            'og:image': 'https://inwestujwfirmy.pl/listing/logo/' + this.listing_id,
            'og:site_name': 'inwestuj-w-firmy',
            'fb:app_id': '3063944677997'
        });
        pl('#facebookbanner').html('<div class="fb-like" data-href="' + this.listing_url + '" data-send="false" data-width="290" data-show-faces="false" data-font="arial"></div>');
  (function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_US/all.js#xfbml=1";
  fjs.parentNode.insertBefore(js, fjs);
  }(document, 'script', 'facebook-jssdk'));
    },

    displayGooglePlus: function() {
        this.addMetaTags('itemprop', {
            'name': this.listing_public_title,
            'description': this.summary,
            'og:image': 'https://inwestujwfirmy.pl/listing/logo/' + this.listing_id
        });
        pl('#gplusbanner').html('<g:plusone size="medium" annotation="inline" width="290" href="' + this.listing_url + '"></g:plusone>');
  (function() {
    var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
    po.src = 'https://apis.google.com/js/plusone.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
  })();
    },

    addMetaTags: function(attributeName, metas) {
        var prop, content, metatag;
        for (prop in metas) {
            content = metas[prop];
            metatag = '<meta ' + attributeName + '="' + prop + '" content="' + content + '"/>';
            pl('head').append(metatag);
        }
    },

    displayDelete: function() {
        var deletable =
                (this.status === 'new' || this.status === 'posted')
                && this.loggedin_profile
                && (this.loggedin_profile.profile_id === this.profile_id || this.loggedin_profile.admin);
        if (deletable) {
            this.bindDeleteButton();
        }
    },

    bindDeleteButton: function() {
        var self = this;
        pl('#deletebox').show();
        pl('#deletebtn').bind({
            click: function() {
                var complete = function() {
                        pl('#deletebtn, #deletecancelbtn').hide();
                        pl('#deletemsg').text('@lang_listing_deleted_home@').show();
                        setTimeout(function() {
                            window.location = '/';
                        }, 3000);
                    },

                    url = '/listing/delete/' + self.listing_id,
                    ajax = new AjaxClass(url, 'deletemsg', complete);
                if (pl('#deletecancelbtn').css('display') === 'none') { // first call
                    pl('#deletemsg, #deletecancelbtn').show();
                }
                else {
                    ajax.setPost();
                    ajax.call();
                }
                return false;
            }
        });
        pl('#deletecancelbtn').bind({
            click: function() {
                pl('#deletemsg, #deletecancelbtn').hide();
                return false;
            }
        });
    },

    displayWithdraw: function() {
        var withdrawable = this.status === 'active' && this.loggedin_profile && this.loggedin_profile.profile_id === this.profile_id;
        if (withdrawable) {
            this.bindWithdrawButton();
        }
    },

    bindWithdrawButton: function() {
        var self = this;
        pl('#withdrawbox').show();
        pl('#withdrawbtn').bind({
            click: function() {
                var complete = function() {
                        pl('#withdrawbtn, #withdrawcancelbtn').hide();
                        pl('#withdrawmsg').text('@lang_listing_withdrawn_home@').show();
                        setTimeout(function() {
                            window.location.reload();
                        }, 3000);
                    },

                    url = '/listing/withdraw/' + self.listing_id,
                    ajax = new AjaxClass(url, 'withdrawmsg', complete);
                if (pl('#withdrawcancelbtn').css('display') === 'none') { // first call
                    pl('#withdrawmsg, #withdrawcancelbtn').show();
                }
                else {
                    ajax.setPost();
                    ajax.call();
                }
                return false;
            }
        });
        pl('#withdrawcancelbtn').bind({
            click: function() {
                pl('#withdrawmsg, #withdrawcancelbtn').hide();
                return false;
            }
        });
    },

    displayApprove: function() {
        var approvable = this.loggedin_profile && this.loggedin_profile.admin && (this.status === 'posted' || this.status === 'frozen');
        if (approvable) {
            this.bindApproveButton();
        }
    },

    bindApproveButton: function() {
        var self = this;
        pl('#approvebox').show();
        pl('#approvebtn').bind({
            click: function() {
                var complete = function() {
                        pl('#approvebtn, #approvecancelbtn').hide();
                        pl('#approvemsg').text('@lang_listing_approved_reloading@').show();
                        setTimeout(function() {
                            window.location.reload();
                        }, 3000);
                    },

                    url = '/listing/activate/' + self.listing_id;
                    ajax = new AjaxClass(url, 'approvemsg', complete);
                if (pl('#approvecancelbtn').css('display') === 'none') { // first call
                    pl('#approvemsg, #approvecancelbtn').show();
                }
                else {
                    ajax.setPost();
                    ajax.call();
                }
                return false;
            }
        });
        pl('#approvecancelbtn').bind({
            click: function() {
                pl('#approvemsg, #approvecancelbtn').hide();
                return false;
            }
        });
    },

    displaySendback: function() {
        var sendbackable = this.loggedin_profile && this.loggedin_profile.admin && (this.status === 'posted' || this.status === 'frozen');
        if (sendbackable) {
            this.bindSendbackButton();
        }
    },

    bindSendbackButton: function() {
        var self = this;
        pl('#sendbackbox').show();
        pl('#sendbackbtn').bind({
            click: function() {
                var complete = function() {
                        pl('#sendbackbtn, #sendbackcancelbtn').hide();
                        pl('#sendbackmsg').text('@lang_listing_sent_back_reloading@').show();
                        setTimeout(function() {
                            window.location.reload();
                        }, 3000);
                    },
                    data = {
                        listing: {
                            id: self.listing_id,
                            message: pl('#sendbacktext').attr('value')
                        }
                    },
                    url = '/listing/send_back/' + self.listing_id,
                    ajax = new AjaxClass(url, 'sendbackmsg', complete);
                if (pl('#sendbackmsg').text() && pl('#sendbackmsg').text().indexOf('Error') !== -1) {
                    pl('#sendbackbtn').hide();
                }
                else if (pl('#sendbackcancelbtn').css('display') === 'none') { // first call
                    pl('#sendbackmsg').text('@lang_are_you_sure@').show();
                    pl('#sendbackcancelbtn').show();
                    pl('#sendbacktext').attr({disabled: 'disabled'});
                }
                else {
                    ajax.setPostData(data);
                    ajax.call();
                }
                return false;
            }
        });
        pl('#sendbackcancelbtn').bind({
            click: function() {
                pl('#sendbackcancelbtn').hide();
                pl('#sendbackmsg').text('').show();
                pl('#sendbackbtn').show();
                pl('#sendbacktext').removeAttr('disabled');
                return false;
            }
        });
    },

    displayFreeze: function() {
        var freezable = this.status === 'active' && this.loggedin_profile && this.loggedin_profile.admin;
        if (freezable) {
            this.bindFreezeButton();
        }
    },

    bindFreezeButton: function() {
        var self = this;
        pl('#freezebox').show();
        pl('#freezebtn').bind({
            click: function() {
                var complete = function() {
                        pl('#freezebtn, #freezecancelbtn').hide();
                        pl('#freezemsg').text('@lang_listing_frozen_reloading@').show();
                        setTimeout(function() {
                            window.location.reload();
                        }, 3000);
                    },
                    data = {
                        listing: {
                            id: self.listing_id,
                            message: pl('#freezetext').attr('value')
                        }
                    },
                    url = '/listing/freeze/' + self.listing_id,
                    ajax = new AjaxClass(url, 'freezemsg', complete);
                if (pl('#freezemsg').text() && pl('#freezemsg').text().indexOf('Error') !== -1) {
                    pl('#freezebtn').hide();
                }
                else if (pl('#freezecancelbtn').css('display') === 'none') { // first call
                    pl('#freezemsg').text('@lang_are_you_sure@').show();
                    pl('#freezecancelbtn').show();
                    pl('#freezetext').attr({disabled: 'disabled'});
                }
                else {
                    ajax.setPostData(data);
                    ajax.call();
                }
                return false;
            }
        });
        pl('#freezecancelbtn').bind({
            click: function() {
                pl('#freezecancelbtn').hide();
                pl('#freezemsg').text('').show();
                pl('#freezebtn').show();
                pl('#freezetext').removeAttr('disabled');
                return false;
            }
        });
    },

    displayPricepoints: function() {
        var self = this,
            pricepoints = new PricepointsClass(self.pricepoints),
            buttonsHTML = pricepoints.buttonsHTML(self.pricepoints, 'pricepoint-description-company', 'purchase-button-company');
        //console.log('displayPricepoints ', self.pricepoints);
        if (buttonsHTML) {
            pl('#pricepoints-wrapper').show();
            if (pl('#pricepoints-wrapper-inner').get(0)) {
                pl('#pricepoints-wrapper-inner').get(0).innerHTML = buttonsHTML;
            }
        }
        else {
            pl('#pricepoints-wrapper').hide();
        }
    }

});

