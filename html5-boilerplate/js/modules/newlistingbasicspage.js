function NewListingBasicsClass() {
    var qs = new QueryStringClass();
    this.importtype = qs.vars.importtype;
    this.importid = qs.vars.importid;
    this.idparam = qs.vars['id'];
    this.base = new NewListingBaseClass();
    this.imagepanel = new ImagePanelClass({ editmode: true });
    this.displayImportType = {
        'AppStore': 'App Store',
        'GooglePlay': 'Google Play',
        'WindowsMarketplace': 'Windows Marketplace',
        'ChromeWebStore': 'Chrome Web Store'
    };
}

pl.implement(NewListingBasicsClass, {

    load: function() {
        var self = this,
            url = this.importtype && this.importid ? '/listing/import' : (this.idparam ? '/listing/get/' + this.idparam : '/listing/create'),
            data = this.importtype && this.importid ? { type: this.importtype, id: this.importid } : null,
            displayImportType = this.displayImportType[this.importtype] || this.importtype,
            complete = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    loggedin_profile = json && json.loggedin_profile ? json.loggedin_profile : {},
                    categories = json && json.categories ? json.categories : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
                self.base.loggedin_profile = loggedin_profile;
                self.storeCategories(categories);
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
        if (data) {
            pl('#newlistingbanner').text('@lang_imported_from@ ' + displayImportType.toUpperCase());
            ajax.ajaxOpts.data = data;
        }
        if (!self.idparam) {
            ajax.setPost();
        }
        ajax.call();
    },

    storeCategories: function(categories) {
        this.categories = categories;
    },

    displayCategories: function() {
        var self = this,
            options = [],
            cat,
            catid;
        for (catid in self.categories) {
            cat = self.categories[catid];
            options.push(cat);
        }
        options.sort();
        self.base.fieldMap['category'].setOptions(options);
    },

    display: function() {
        var loggedin_profile_id = this.base.loggedin_profile ? this.base.loggedin_profile.profile_id : null,
            listing_profile_id = this.base.listing ? this.base.listing.profile_id : null,
            listing_id = this.base.listing ? this.base.listing.listing_id : null,
            editable = loggedin_profile_id && listing_profile_id && loggedin_profile_id === listing_profile_id;
        if (!listing_id) {
            document.location = '/';
        }
        else if (!editable) {
            document.location = '/company-page.html?id=' + this.base.listing.listing_id;
        }
        else if (!this.bound) {
            this.displayButtons();
            this.bindEvents();
            this.bound = true;
        }
    },

    displayButtons: function() {
        this.displayAskFundingButton();
        this.displayVideoButton();
        this.displayValuationButton();
        this.displayCashFlowButton();
        this.displayModelButton();
        this.displayPresentationButton();
        this.displayDocumentButton();
        this.displayContributionsButton();
    },

    displayAskFundingButton: function() {
        if (this.base.listing.asked_fund) {
            pl('#askfundingbutton').text('@lang_edit_funding@');
        }
    },

    displayVideoButton: function() {
        if (this.base.listing.video) {
            pl('#videobutton').text('@lang_edit_video@');
        }
    },

    displayValuationButton: function() {
        if (MicroListingClass.prototype.getHasValuation(this.base.listing)) {
            pl('#valuationbutton').text('@lang_edit_valuation@');
        }
    },

    displayCashFlowButton: function() {
        if (MicroListingClass.prototype.getHasCashFlow(this.base.listing)) {
            pl('#cashflowbutton').text('@lang_edit_cashflow@');
        }
    },

    displayModelButton: function() {
        if (MicroListingClass.prototype.getHasBmc(this.base.listing)) {
            pl('#modelbutton').text('@lang_edit_model@');
        }
    },

    displayPresentationButton: function() {
        if (MicroListingClass.prototype.getHasIp(this.base.listing)) {
            pl('#presentationbutton').text('@lang_presentation@');
        }
    },

    displayDocumentButton: function() {
        if (MicroListingClass.prototype.getHasDoc(this.base.listing)) {
            pl('#documentbutton').text('@lang_edit_documents@');
        }
    },

    displayContributionsButton: function() {
    	pl('#editcontributions_a').attr({ href: '/company-members-page.html?id=' + this.base.listing.listing_id });
        if (MicroListingClass.prototype.getHasContributions(this.base.listing)) {
            pl('#editcontributions').text('@lang_members@');
        }
    },

    bindEvents: function() {
        var textFields = ['title', 'type', 'platform', 'category', 'stage', 'currency', 'address', 'mantra', 'summary'],
            validators = {
                title: ValidatorClass.prototype.isNotEmpty,
                type: ValidatorClass.prototype.isSelected,
                platform: ValidatorClass.prototype.isSelected,
                category: ValidatorClass.prototype.isSelected,
                stage: ValidatorClass.prototype.isSelected,
                currency: ValidatorClass.prototype.isSelected,
                mantra: ValidatorClass.prototype.makeLengthChecker(5, 140),
                summary: ValidatorClass.prototype.makeLengthChecker(50, 2000),
                address: ValidatorClass.prototype.isNotEmpty
            },
            classes = {
                title: TextFieldClass,
                type: SelectFieldClass,
                platform: SelectFieldClass,
                category: SelectFieldClass,
                stage: SelectFieldClass,
                currency: SelectFieldClass,
                mantra: TextFieldClass,
                summary: TextFieldClass,
                address: TextFieldClass
            },
            typeOptions = [ ['application', '@lang_application@'], ['company', '@lang_company@'] ],
            stageOptions = [ ['concept', '@lang_concept@'], ['startup', '@lang_startup@'], ['established', '@lang_established@' ] ],
            currencyOptions = [ ['pln', '@lang_currency_pln@'], ['eur', '@lang_currency_eur@'], ['gbp', '@lang_currency_gbp@'], ['usd', '@lang_currency_usd@'] ],
            platforms = [ 'ios', 'android', 'windows_phone', 'website', 'desktop', 'other' ],
            platformOptions = [],
            platform,
            i,
            id,
            field,
            addr,
            updater;
        this.base.fields = [];
        this.base.fieldMap = {};
        for (i = 0; i < platforms.length; i++) {
            platform = platforms[i];
            platformOptions.push([ platform, PlatformClass.prototype.displayName(platform) ]);
        }
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            updater = this.base.getUpdater(id);
            field = new (classes[id])(id, this.base.listing[id], updater, 'newlistingbasicsmsg');
            if (this.base.displayNameOverrides[id]) {
                field.fieldBase.setDisplayName(this.base.displayNameOverrides[id]);
            }
            field.fieldBase.addValidator(validators[id]);
            field.fieldBase.validator.postValidator = this.base.genDisplayCalculatedIfValid(field);
            if (id !== 'address') {
                field.bindEvents();
            }
            this.base.fields.push(field);
            this.base.fieldMap[id] = field;
        } 
        this.base.fieldMap['type'].setOptionsWithValues(typeOptions);
        this.base.fieldMap['platform'].setOptionsWithValues(platformOptions);
        this.base.fieldMap['stage'].setOptionsWithValues(stageOptions);
        this.base.fieldMap['currency'].setOptionsWithValues(currencyOptions);
        this.bindLogo();
        this.bindImages();
        this.setUploadUrls();
        this.displayCategories();
        this.addMap(this.genPlaceUpdater());
        this.bindAddressEnterSubmit();
        this.base.bindNavButtons();
        this.base.bindTitleInfo();
        this.base.bindInfoButtons();
        if (this.base.listing.status === 'new') {
            pl('.bottombackbutton').hide(); // no back for basic page
        }
        pl('#newlistingbasicswrapper').show();
    },

    bindLogo: function() {
        var self = this,
            datauri = this.base.listing.logo,
            postLogo = function(json) {
                var success = false;
                if (json && json.listing) {
                    self.base.listing = json.listing;
                    self.setUploadUrls();
                    if (self.base.listing.logo) {
                        self.displayLogo(self.base.listing.logo);    
                        self.base.displayCalculated();
                        success = true;
                    }
                }
                pl('#logo_url, #logouploadfile').attr({value: ''});
                if (success) {
                    pl('#logomsg').removeClass('errorcolor').removeClass('inprogress').addClass('successful').text('@lang_logo_uploaded@');
                }
                else {
                    pl('#logomsg').addClass('errorcolor').text('@lang_upload_logo_error@');
                }
            },
            logoUpdater = this.base.getUpdater('logo_url', null, postLogo),
            logoURLField = new TextFieldClass('logo_url', null, logoUpdater, 'logomsg');
        pl('#logouploadiframe').bind({
            load: function() {
                var iframe = pl('#logouploadiframe').get(0),
                    iframehtml = iframe && iframe.contentDocument && iframe.contentDocument.body ? iframe.contentDocument.body.innerHTML : '',
                    uploadurlmatch = iframehtml.match(/upload_url&gt;(.*)&lt;\/upload_url/),
                    uploadurl = uploadurlmatch && uploadurlmatch.length === 2 ? uploadurlmatch[1] : null,
                    dataurimatch = iframehtml.match(/value&gt;(.*)&lt;\/value/),
                    datauri = dataurimatch && dataurimatch.length === 2 ? dataurimatch[1] : null,
                    iframeloc = iframe.contentWindow.location,
                    errorMsg = iframeloc && iframeloc.search && iframeloc.search ? decodeURIComponent(iframeloc.search.replace(/^[?]errorMsg=/, '')) : null;
                if (uploadurl && uploadurl !== 'null') {
                    self.base.listing.upload_url = uploadurl;
                    self.setUploadUrls();
                }
                if (uploadurl && datauri && !errorMsg) {
                    self.base.listing.logo = datauri;
                    self.displayLogo(datauri);
                    self.base.displayCalculated();
                    pl('#logomsg').removeClass('errorcolor').removeClass('inprogress').addClass('successful').text('@lang_logo_uploaded@');
                }
                else {
                    pl('#logomsg').addClass('errorcolor').text(errorMsg || '@lang_upload_logo_error@');
                }
                pl('#logo_url, #logouploadfile').attr({value: ''});
            }
        });
        pl('#logouploadfile').bind({
            change: function() {
                pl('#logomsg').removeClass('errorcolor').addClass('inprogress').text('@lang_uploading@');
                pl('#logouploadform').get(0).submit();
                return false;
            }
        });
        logoURLField.fieldBase.setDisplayName('@lang_logo_url@');
        logoURLField.fieldBase.addValidator(ValidatorClass.prototype.isURLEmptyOk);
        logoURLField.fieldBase.isEmptyNoUpdate = true;
        logoURLField.bindEvents();
        self.displayLogo(datauri);
    },

    bindImages: function() {
        var self = this,
            postPic = function(json) { // FIXME
                var success = false,
                    picnum = pl('#picnum').text();
                if (json && json.listing) {
                    self.base.listing = json.listing;
                    self.setUploadUrls();
                    if (self.base.listing['pic' + picnum]) {
                        self.imagepanel.enableImage(picnum);
                        success = true;
                    }
                }
                pl('#pic_url, #picuploadfile').attr({value: ''});
                if (success) {
                    pl('#picmsg').removeClass('errorcolor').removeClass('inprogress').addClass('successful').text('@lang_image_uploaded@');
                }
                else {
                    pl('#picmsg').addClass('errorcolor').text('@lang_upload_image_error@');
                }
            },
            picUpdater = this.base.getUpdater('pic_url', null, postPic, null, function() { return 'pic' + pl('#picnum').text() + '_url' }),
            picURLField = new TextFieldClass('pic_url', null, picUpdater, 'picmsg');
        pl('#pic_url, #picuploadfile').bind('click', function() { self.imagepanel.runningSlideshow = false; });
        pl('#picuploadiframe').bind({
            load: function() {
                var iframe = pl('#picuploadiframe').get(0),
                    iframehtml = iframe && iframe.contentDocument && iframe.contentDocument.body ? iframe.contentDocument.body.innerHTML : '',
                    uploadurlmatch = iframehtml.match(/upload_url&gt;(.*)&lt;\/upload_url/),
                    uploadurl = uploadurlmatch && uploadurlmatch.length === 2 ? uploadurlmatch[1] : null,
                    picnum = pl('#picnum').text(),
                    picurl = '/listing/picture/' + self.base.listing.listing_id + '/' + picnum,
                    iframeloc = iframe.contentWindow.location,
                    errorMsg = iframeloc && iframeloc.search && iframeloc.search ? decodeURIComponent(iframeloc.search.replace(/^[?]errorMsg=/, '')) : null;
                if (uploadurl && uploadurl !== 'null') {
                    self.base.listing.upload_url = uploadurl;
                    self.setUploadUrls();
                }
                if (uploadurl && picurl && !errorMsg) {
                    self.base.listing['pic' + picnum] = true;
                    self.imagepanel.enableImage(picnum);
                    pl('#picmsg').removeClass('errorcolor').removeClass('inprogress').addClass('successful').text('@lang_image_uploaded@');
                }
                else {
                    pl('#picmsg').addClass('errorcolor').text(errorMsg || '@lang_upload_image_error@');
                }
                pl('#pic_url, #picuploadfile').attr({value: ''});
            }
        });
        pl('#picuploadfile').bind({
            change: function() {
                pl('#picmsg').removeClass('errorcolor').addClass('inprogress').text('@lang_uploading@');
                pl('#picuploadform').get(0).submit();
                return false;
            }
        });
        pl('#deleteimagebutton').bind('click', function() {
            var picnum = pl('#picnum').text();
            self.imagepanel.deleteImage(picnum);
        });
        picURLField.fieldBase.setDisplayName('IMAGE URL');
        picURLField.fieldBase.addValidator(ValidatorClass.prototype.isURLEmptyOk);
        picURLField.fieldBase.isEmptyNoUpdate = true;
        picURLField.bindEvents();
        this.displayImages();
    },

    displayLogo: function(dataurl) {
        var url = dataurl && dataurl.indexOf('data:') === 0 ? dataurl : null,
            logobg = url ? 'url(' + url + ') no-repeat scroll center top transparent' : null;
        if (url) {
            pl('#logoimg').css({ background: logobg });
        }
/*
        else {
            pl('#logoimg');
        }
*/
    },

    setUploadUrls: function() {
        pl('#logouploadform, #picuploadform').attr({action: this.base.listing.upload_url});
        pl('#logo_url, #pic_url, #logouploadfile, #picuploadfile').attr({value: ''});
    },

    displayImages: function() {
        this.imagepanel.setListing(this.base.listing).display();
    },

    bindAddressEnterSubmit: function() {
        if (!window['google']) {
            return;
        }
        var self = this;
        pl('#address').bind({
            keyup: function(e) {
                var evt = new EventClass(e),
                    input,
                    geocoder;
                if (evt.keyCode() === 13) {
                    address = pl('#address').attr('value'),
                    geocoder = google ? new google.maps.Geocoder() : null;
                    if (geocoder) {
                        geocoder.geocode({address: address}, function(results, status) {
                            if (status == google.maps.GeocoderStatus.OK) {
                                self.genPlaceUpdater()(results[0]);
                            }
                            else {
                                pl('#newlistingbasicsmsg').html('<span class="attention">@lang_geocode_error@: ' + status + '</span>');
                            }
                        });
                    }
                    return false;
                }
                else {
                    return true;
                }
            }
         });
    },

    genPlaceUpdater: function() {
        var self = this;
        return function(place) {
            var completeFunc = function(json) {
                    if (!json.listing) {
                        return;
                    }
                    self.base.listing.address = json.listing.address;
                    self.base.listing.brief_address = json.listing.brief_address;
                    self.base.listing.latitude = json.listing.latitude;
                    self.base.listing.longitude = json.listing.longitude;
                    pl('#address').attr({value: json.listing.address});
                    self.base.displayCalculated();
                },
                data = { listing: {
                		id: self.base.listing.listing_id,
                        update_address: place
                       } },
                ajax = new AjaxClass('/listing/update_address', 'newlistingbasicsmsg', completeFunc);
            ajax.setPostData(data);
            ajax.call();
        };
    },

    addMap: function(placeUpdater) {
        if (!window['google']) {
            return;
        }
        var self = this,
            lat = this.base.listing.latitude !== null ? this.base.listing.latitude : 51.4791,
            lng = this.base.listing.longitude !== null ? this.base.listing.longitude : 0,
            autoField = pl('#address').get(0),
            autoOptions = {},
            mapField = pl('#addressmap').get(0),
            mapOptions = {
                center: (google ? new google.maps.LatLng(lat, lng) : null),
                zoom: this.base.listing.address ? 13 : 7,
                mapTypeId: (google ? google.maps.MapTypeId.ROADMAP : null),
                draggable: false,
                scrollwheel: false,
                disableDoubleClickZoom: true,
                disableDefaultUI: true,
                styles: [
                    {
                        featureType: 'landscape',
                        elementType: 'all',
                        stylers: [
                            { "color": "#ffffff" },
                            { saturation: -100 },
                            { lightness: 4 },
                            { visibility: 'on' }
                        ]
                    },{
                        featureType: 'road.highway',
                        elementType: 'all',
                        stylers: [
                            { "color": "#4cc7df"},
                            { saturation: 100 },
                            { lightness: -7 },
                            { visibility: 'on' }
                        ]
                    },{
                        featureType: 'road.arterial',
                        elementType: 'all',
                        stylers: [
                            { "color": "#4cc7df" },
                            { saturation: -30 },
                            { lightness: -3 },
                            { visibility: 'on' }
                        ]
                    },{
                        featureType: 'road.local',
                        elementType: 'all',
                        stylers: [
                            { "color": "#ffffff" },
                            { saturation: -30 },
                            { lightness: -3 },
                            { visibility: 'on' }
                        ]
                    },{
                        featureType: 'landscape.natural',
                        elementType: 'all',
                        stylers: [
                            { "color": "#ffffff" },
                            { saturation: -30 },
                            { lightness: -3 },
                            { visibility: 'on' }
                        ]
                    },{
                        featureType: 'poi.park',
                        elementType: 'all',
                        stylers: [
                            { "color": "#ffffff" },
                            { saturation: -30 },
                            { lightness: -3 },
                            { visibility: 'on' }
                        ]
                    }
                ]
            },
            addressAuto = google ? new google.maps.places.Autocomplete(autoField, autoOptions) : null,
            addressMap = google ? new google.maps.Map(mapField, mapOptions) : null,
            latLng = google ? new google.maps.LatLng(lat, lng) : null,
            marker = google ? new google.maps.Marker({
                cursor: 'pointer',
                position: latLng,
                raiseOnDrag: false,
                icon: {
                    anchor: (google ? new google.maps.Point(25,15) : null), // wspolzedne poczatku na ikonie tu center statku
                    url: 'img/statek-ico.png',
                    map: this.addressMap
                }}) : null;
        if (addressAuto) {
            addressAuto.bindTo('bounds', addressMap);
        }
        if (google) {
            google.maps.event.addListener(addressAuto, 'place_changed', function() {
                var place = addressAuto.getPlace(),
                    image;
                if (place && place.geometry && place.geometry.location) {
                    placeUpdater(place);
                    if (place.geometry.viewport) {
                        addressMap.fitBounds(place.geometry.viewport);
                        image = new google.maps.MarkerImage(
                            place.icon,
                            new google.maps.Size(71, 71),
                            new google.maps.Point(0, 0),
                            new google.maps.Point(17, 34),
                            new google.maps.Size(35, 35));
                        marker.setIcon(image);
                        marker.setPosition(place.geometry.location);
                    }
                    else {
                        addressMap.setCenter(place.geometry.location);
                        addressMap.setZoom(17);
                    }
                }
            });
        }
    }

});

function NewListingPageClass() {};

pl.implement(NewListingPageClass, {

    loadPage: function() {
        var newlisting = new NewListingBasicsClass();
        newlisting.load();
    }

});

(new NewListingPageClass()).loadPage();

