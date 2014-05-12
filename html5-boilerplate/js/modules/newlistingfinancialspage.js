function NewListingFinancialsClass() {
    var qs = new QueryStringClass();
    this.idparam = qs.vars['id'];
    this.base = new NewListingBaseClass();
}
pl.implement(NewListingFinancialsClass, {
    load: function() {
        var self = this,
            complete = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    profile = json && json.loggedin_profile ? json.loggedin_profile : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
                self.loggedin_profile = profile;
                self.display();
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader, .companyheader').hide();
                pl('.errorwrapper').show();
            },
            url = this.idparam ? '/listing/get/' + this.idparam : '/listing/create',
            ajax = new AjaxClass(url, 'newlistingmsg', complete, null, null, error);
        if (!self.idparam) {
            ajax.setPost();
        }
        ajax.call();
    },
    display: function() {
        var status = this.base.listing.status;
        if (!this.base.isMyListing(this.loggedin_profile)) {
            document.location = '/company-page.html?id=' + this.base.listing_id;
        }
        if (!this.bound) {
            this.bindEvents();
            this.bound = true;
        }
    },
    bindEvents: function() {
        var self = this,
            textFields = ['asked_fund', 'suggested_amt', 'suggested_pct', 'founders'],
            msgids = {
                asked_fund: 'newlistingaskmsg',
                suggested_amt: 'newlistingoffermsg',
                suggested_pct: 'newlistingoffermsg',
                founders: 'newlistingfoundersmsg'
            },
            validators = {
                asked_fund: ValidatorClass.prototype.isCheckedVal,
                suggested_amt: ValidatorClass.prototype.genIsNumberBetween(100, 500000),
                suggested_pct: ValidatorClass.prototype.genIsNumberBetween(1, 100),
                founders: ValidatorClass.prototype.isNotEmpty
            },
            classes = {
                asked_fund: CheckboxFieldClass,
                suggested_amt: TextFieldClass,
                suggested_pct: TextFieldClass,
                founders: TextFieldClass
            },
            names = {
                asked_fund: 'ALLOW BIDS',
                suggested_amt: 'ASKING',
                suggested_pct: 'PERCENT',
                founders: 'FOUNDERS'
            },
            preValidators = {
                suggested_amt: CurrencyClass.prototype.clean,
                suggested_pct: PercentClass.prototype.clean
            },
            id,
            cleaner,
            offerboxdisplay = function() {
                self.displayOfferBox();
            },
            field;
        self.base.fields = [];
        self.base.fieldMap = {};
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            cleaner = preValidators[id];
            if (id === 'asked_fund') {
                field = new (classes[id])(id, self.base.listing[id], self.base.getUpdater(id, cleaner, offerboxdisplay), msgids[id]);
            }
            else if (cleaner) {
                field = new (classes[id])(id, self.base.listing[id], self.base.getUpdater(id, cleaner), msgids[id]);
            }
            else {
                field = new (classes[id])(id, self.base.listing[id], self.base.getUpdater(id), msgids[id]);
            }
            field.fieldBase.setDisplayName(names[id]);
            field.fieldBase.addValidator(validators[id]);
            if (preValidators[id]) {
                field.fieldBase.validator.preValidateTransform = preValidators[id];
            }
            if (id === 'asked_fund') {
                field.fieldBase.validator.postValidator = self.genDisplayAskedEffects(field);
            }
            else if (id === 'suggested_amt') {
                field.fieldBase.validator.postValidator = self.genDisplayCalculatedIfValidAmt(field);
            }
            else if (id === 'suggested_pct') {
                field.fieldBase.validator.postValidator = self.genDisplayCalculatedIfValidPct(field);
            }
            field.bindEvents();
            self.base.fields.push(field);
            self.base.fieldMap[id] = field;
        }
        self.base.fieldMap['suggested_amt'].validate();
        self.base.fieldMap['suggested_pct'].validate();
        self.displayCalculatedIfValid();
        self.displayOfferBox();
        self.bindAskingButtons();
        self.base.bindNavButtons(self.genNextValidator());
        self.base.bindTitleInfo();
        self.base.bindInfoButtons();
        pl('#newlistingfinancialswrapper').show();
    },
    genNextValidator: function() {
        var self = this;
        return function() {
            var asked_fund = pl('#asked_fund').attr('checked') ? true : false,
                msgs = asked_fund ? self.base.validate() : [];
            /*
            if (!self.base.listing.presentation_id) {
                msgs.push("SLIDE DECK: you must have a presentation.");
            }
            if (!self.base.listing.business_plan_id) {
                msgs.push("BUSINESS PLAN: you must have a business plan.");
            }
            if (!self.base.listing.presentation_id) {
                msgs.push("FINANCIALS: you must have a financial document.");
            }
            */
            return msgs;
        };
    },
    genDisplayAskedEffects: function(field) {
        var f1 = this.base.genDisplayCalculatedIfValid(field);
        var self = this;
        return function(result) {
            f1();
            self.displayOfferBox();
        }
    },
    displayOfferBox: function() {
        var fnd = this.base.fieldMap.asked_fund.fieldBase.value;
        if (fnd) {
            pl('#askfundstatus').text('@lang_asking_funds_msg@');
            pl('#offerwrapper').addClass('offerwrapperdisplay');
        }
        else {
            pl('#askfundstatus').text('@lang_not_asking_funds_msg@');
            pl('#offerwrapper').removeClass('offerwrapperdisplay');
        }
        this.displayCalculatedIfValid();
    },
    genDisplayCalculatedIfValidAmt: function(field) {
        var self = this;
            f1 = this.base.genDisplayCalculatedIfValid(field);
        return function(result, val) {
            self.displayIfValidAmt(result, val);
            f1();
            self.displayCalculatedIfValid();
        }
    },
    genDisplayCalculatedIfValidPct: function(field) {
        var self = this;
            f1 = this.base.genDisplayCalculatedIfValid(field);
        return function(result, val) {
            self.displayIfValidPct(result, val);
            f1();
            self.displayCalculatedIfValid();
        }
    },
    displayIfValidAmt: function(result, val) {
        var self = this,
            currency = self.base.listing.currency;
        var fmt = CurrencyClass.prototype.format(val, currency);
        if (result === 0) {
            pl('#suggested_amt').attr({value: fmt});
        }
    },
    displayIfValidPct: function(result, val) {
        var fmt = PercentClass.prototype.format(val);
        if (result === 0) {
            pl('#suggested_pct').attr({value: fmt + '%'});
        }
    },
    displayCalculatedIfValid: function() {
        var self = this,
            currency = self.base.listing.currency,
            fnd = pl('#asked_fund').hasClass('checkboxcheckedicon') ? true : false,
            amt = CurrencyClass.prototype.clean(pl('#suggested_amt').attr('value')) || 0,
            pct = PercentClass.prototype.clean(pl('#suggested_pct').attr('value')) || 0,
            val = pct ? Math.floor(Math.floor(100 * amt / pct)) : 0,
            cur = CurrencyClass.prototype.format(CurrencyClass.prototype.clean(val), currency),
            dis = fnd && cur ? cur : '';
        pl('#suggested_val').text(dis);
    },
    bindAskingButtons: function() {
        var self = this;
        pl('.askingamtbtn').bind('click', function(e) {
            var amt = e.target && pl(e.target).text();
            if (amt) {
                pl('#suggested_amt').attr('value', amt);
                self.base.fieldMap.suggested_amt.update()
            }
        });
        pl('.askingpctbtn').bind('click', function(e) {
            var pct = e.target && pl(e.target).text();
            if (pct) {
                pl('#suggested_pct').attr('value', pct);
                self.base.fieldMap.suggested_pct.update()
            }
        });
    }

});

(new NewListingFinancialsClass()).load();

