function CashFlowClass() {
    this.cashflow_data = { company: {}};
    this.currencyFields = {
        initial_investment: 1,
        development_cost: 1,
        initial_sales: 1,
        fixed_expenses: 1
    };
    this.numberFields = {
        time_to_market: 1
    };
    this.percentFields = {
        gross_margin: 1,
        growth_rate: 1
    }
}

pl.implement(CashFlowClass, {

    store: function(listing) {
        var vdata = listing.cashflow_data ? JSON.parse(listing.cashflow_data) : {},
            k, v;
        this.currency = listing.currency;
        this.groupingSymbol = this.currency === 'pln' ? ' ' : ',';
        if (vdata) {
            for (k in vdata) {
                v = vdata[k];
                this.cashflow_data[k] = v;
            }
        }
        if (this.cashflow_data['company'] === undefined) {
            this.cashflow_data['company'] = {};
        }
    },

    removeNodeChildren: function(selector) {
        var node = pl(selector).get(0);
        while(node.firstChild) {
            node.removeChild(node.firstChild);
        }
    },

    generateData: function() {
        var self = this,

            company = self.cashflow_data.company,
            currencyFormatter = new google.visualization.NumberFormat({
                groupingSymbol: self.groupingSymbol,
                negativeColor: 'red',
                negativeParens: true,
                fractionDigits: 0
            }),

            initial_investment = company.initial_investment || 0,
            development_cost = company.development_cost || 0,
            time_to_market = company.time_to_market || 0,
            initial_sales = company.initial_sales || 0,
            gross_margin = company.gross_margin || 0,
            growth_rate = company.growth_rate || 0,
            fixed_expenses = company.fixed_expenses || 0,

            tableData = new google.visualization.DataTable(),
            chartData = new google.visualization.DataTable(),

            fmt = function(s) {
                var t = '' + s;
                return t.replace(/ /g, '&nbsp;');
            },
            months_to_market_row = [fmt('@lang_months_to_market@')],
            time_to_market_left,
            sales_row = ['@lang_sales@'],
            sales,
            investment_row = ['@lang_investment@'],
            investment,
            cash_in_row = [fmt('<b>@lang_cash_in@</b>')],
            cash_in,

            cost_of_goods_sold_row = [fmt('@lang_cost_of_goods_sold@')],
            cost_of_goods_sold,
            dev_cost_row = [fmt('@lang_dev_cost@')],
            dev_cost,
            fixed_exp_row = [fmt('@lang_fixed_exp@')],
            fixed_exp,
            cash_out_row = [fmt('<b>@lang_cash_out@</b>')],
            cash_out,

            net_cash_row = [fmt('<b>@lang_net_cash@</b>')],
            net_cash,
            balance_row = ['<b>@lang_balance@</b>'],
            balance,

            tableRows = [],
            chartRows = [],
            month,
            shortMonth,
            monthTitle,
            numMonths = 24;

        tableData.addColumn('string', '@lang_month@');

        chartData.addColumn('string', '@lang_month@');
        chartData.addColumn('number', '@lang_net_cash@');
        chartData.addColumn('number', '@lang_balance@');

        balance = 0;

        for (month = 1; month <= numMonths; month++) {
            monthTitle = '@lang_month@' + '&nbsp;' + month;
            tableData.addColumn('number', monthTitle);

            time_to_market_left = time_to_market < month ? 0 : time_to_market - month + 1;
            months_to_market_row.push(time_to_market_left);

            sales = time_to_market_left > 0 ? 0 : initial_sales * Math.pow(1 + growth_rate / 100, month - time_to_market - 1);
            sales_row.push(sales);

            investment = month > 1 ? 0 : initial_investment;
            investment_row.push(investment);

            cash_in = sales + investment;
            cash_in_row.push(cash_in);

            cost_of_goods_sold = sales * (1 - gross_margin / 100);
            cost_of_goods_sold_row.push(cost_of_goods_sold);

            dev_cost = time_to_market_left == 0 ? 0 : development_cost / time_to_market;
            dev_cost_row.push(dev_cost);

            fixed_exp = fixed_expenses;
            fixed_exp_row.push(fixed_exp);

            cash_out = cost_of_goods_sold + dev_cost + fixed_exp;
            cash_out_row.push(cash_out);

            net_cash = cash_in - cash_out;
            net_cash_row.push(net_cash);

            balance = balance + net_cash;
            balance_row.push(balance);

            shortMonth = '' + month;
            chartData.addRow([ shortMonth, net_cash, balance ]);
        }

        tableRows.push(months_to_market_row);
        //tableRows.push(blank_row);
        tableRows.push(sales_row);
        tableRows.push(investment_row);
        tableRows.push(cash_in_row);
        //tableRows.push(blank_row);
        tableRows.push(cost_of_goods_sold_row);
        tableRows.push(dev_cost_row);
        tableRows.push(fixed_exp_row);
        tableRows.push(cash_out_row);
        //tableRows.push(blank_row);
        tableRows.push(net_cash_row);
        tableRows.push(balance_row);
        tableData.addRows(tableRows);

        for (month = 1; month <= numMonths; month++) {
            currencyFormatter.format(tableData, month);
        }
        currencyFormatter.format(chartData, 1);
        currencyFormatter.format(chartData, 2);

        self.tableData = tableData;
        self.chartData = chartData;
    },

    drawTable: function() {
        var self = this,
            dummy = self.removeNodeChildren('#table_div'),
            table = new google.visualization.Table(document.getElementById('table_div')),
            options = {
                title: '@lang_cashflow_table_title@',
                allowHtml: true
            };
        table.draw(self.tableData, options);
    },

    drawChart: function() {
        var self = this,
            dummy = self.removeNodeChildren('#chart_div'),
            chart = new google.visualization.ComboChart(document.getElementById('chart_div')),
            currency = self.currency.toUpperCase(),
            options = {
                title: '@lang_cashflow_chart_title@',
                vAxis: { title: '@lang_balance@ (' + currency + ')' },
                hAxis: { title: '@lang_month@' },
                series: {
                    0: { color: 'green', type: 'bars' },
                    1: { color: 'black', type: 'line' }
                }
            };
        chart.draw(self.chartData, options);
    },

    valueCompany: function() {
        var self = this,
            callback = function() {
                self.generateData();
                self.drawTable();
                self.drawChart();
            },
            options = {
                'callback': callback,
                'packages': [ 'table', 'corechart' ]
            };
        setTimeout(function(){
                google.load('visualization', '1', options);
            },
            250);
    }

});

function CashFlowPageClass() {
    this.listing_id = (new QueryStringClass()).vars.id;
    this.cashflow = new CashFlowClass();
}
pl.implement(CashFlowPageClass, {
    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('model');
                header.setLogin(json);
                companybanner.display(json);
                self.display(json.listing);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader, .companyheader').hide();
                pl('.errorwrapper').show();
            },
            ajax = new AjaxClass('/listing/get/' + this.listing_id, 'cashflowmsg', complete, null, null, error);
        ajax.call();
    },

    display: function(listing) {
        this.cashflow.store(listing);
        this.displayCashFlowData();
        this.cashflow.valueCompany();
    },

    displayCashFlowData: function() {
        var vdata = this.cashflow.cashflow_data;
        this.displayFieldMap(vdata.company);
    },

    displayFieldMap: function(map) {
        var k, v;
        for (k in map) {
            v = map[k];
            if (k in this.cashflow.currencyFields) {
                this.displayCurrencyField(k, v);
            }
            else if (k in this.cashflow.numberFields) {
                this.displayNumberField(k, v);
            }
            else if (k in this.cashflow.percentFields) {
                this.displayPercentField(k, v);
            }
            else {
                this.displayTextField(k, v);
            }
        }
    },

    displayCurrencyField: function(id, val) {
        pl('#' + id).text(val !== undefined ? CurrencyClass.prototype.format(val, this.cashflow.currency) : '');
    },

    displayNumberField: function(id, val) {
        pl('#' + id).text(val !== undefined ? NumberClass.prototype.formatText(val) : '');
    },

    displayPercentField: function(id, val) {
        pl('#' + id).text(val !== undefined ? NumberClass.prototype.formatText(val) + '%' : '');
    },

    displayTextField: function(id, val) {
        pl('#' + id).text(SafeStringClass.prototype.htmlEntities(val.toString()));
    }

});

function NewListingCashFlowClass() {
    this.listing_id = (new QueryStringClass()).vars.id;
    this.base = new NewListingBaseClass();
    this.bound = {};
    this.cashflow = new CashFlowClass();
}
pl.implement(NewListingCashFlowClass, {
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
                self.cashflow.store(listing);
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
        var self = this;
        pl('#listingtype').text(this.base.listing.type.toUpperCase());
        this.displayCashFlowData();
        this.loadValuesFromInput();
        this.base.bindBackButton();
        this.base.bindPreviewButton();
        this.bindSaveButton();
        this.bindInput();
        this.cashflow.valueCompany();
    },

    displayCashFlowData: function() {
        this.displayFieldMap(this.cashflow.cashflow_data.company);
    },

    displayFieldMap: function(map) {
        var k, v;
        for (k in map) {
            v = map[k];
            if (k in this.cashflow.currencyFields) {
                this.displayCurrencyField(k, v);
            }
            else if (k in this.cashflow.numberFields) {
                this.displayNumberField(k, v);
            }
            else if (k in this.cashflow.percentFields) {
                this.displayPercentField(k, v);
            }
            else {
                this.displayTextField(k, v);
            }
        }
    },

    displayCurrencyField: function(id, val) {
        pl('#' + id).attr('value', val !== undefined ? CurrencyClass.prototype.format(val, this.cashflow.currency) : '');
    },

    displayNumberField: function(id, val) {
        pl('#' + id).attr('value', val !== undefined ? NumberClass.prototype.formatText(val) : '');
    },

    displayPercentField: function(id, val) {
        //console.log('displayPercentField id,val', id, val);
        pl('#' + id).attr('value', val !== undefined ? NumberClass.prototype.formatText(val) + '%' : '');
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
                        cashflow_data: JSON.stringify(self.cashflow.cashflow_data)
                    }
                },
                complete = function() {
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

    loadValuesFromInput: function() {
        var cf = this.cashflow,
            c = {},
            k;

        cf.cashflow_data = { company: c };

        cf.currencyFields = {
            initial_investment: 1,
            development_cost: 1,
            initial_sales: 1,
            fixed_expenses: 1
        };
        cf.numberFields = {
            time_to_market: 1
        };
        cf.percentFields = {
            gross_margin: 1,
            growth_rate: 1
        };

        for (k in cf.currencyFields) {
            c[k] = Math.max(CurrencyClass.prototype.clean(pl('#' + k).attr('value')), 0);
        }
        for (k in cf.numberFields) {
            c[k] = Math.max(NumberClass.prototype.clean(pl('#' + k).attr('value')), 0);
        }
        for (k in cf.percentFields) {
            c[k] = Math.max(NumberClass.prototype.clean(pl('#' + k).attr('value')), 0);
        }
    },

    bindInput: function() {
        var self = this,
            evaluate = function() {
                self.loadValuesFromInput();
                self.cashflow.valueCompany();
                return false;
            };
        pl('.cashflowinput').bind({
            focus: evaluate,
            blur: evaluate,
            keyup: evaluate,
            change: evaluate
        });
    }

});


