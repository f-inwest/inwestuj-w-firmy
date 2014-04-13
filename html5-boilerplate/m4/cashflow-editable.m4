`
<div class="span-16">
    <div class="header-content" style="margin-bottom: 0; height: 45px;">
        <div class="header-title">@lang_cashflow_wizard@</div>
    </div>
</div>

<div class="span-8 last">
    <a href="#" class="backbuttonlink span-3 investbutton backbutton">
        &lt;&lt; @lang_back@
    </a>
</div>

<div class="span-24 cashflowwrapper">

    <div>

        <div class="formitem clear firstinputitem">
            <p class="formhelptext">
                @lang_cashflow_intro@
            </p>
        </div>

    </div>

    <div class="cashflowpanel">

        <div class="formitem clear firstinputitem">
            <p class="formhelptext">
                @lang_company_cashflow_intro@
            </p>
        </div>

        <div class="formitem clear">
            <span class="inputlabel cashflowlabel">@lang_initial_investment@</span>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="initial_investment" id="initial_investment" maxlength="20"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_initial_investment_help@
            </span>
        </div>
        
        <div class="formitem clear">
            <span class="inputlabel cashflowlabel">@lang_development_cost@</span>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="development_cost" id="development_cost" maxlength="20"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_development_cost_help@
            </span>
        </div>
        
        <div class="formitem clear">
            <span class="inputlabel cashflowlabel">@lang_initial_sales@</span>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="initial_sales" id="initial_sales" maxlength="20"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_initial_sales_help@
            </span>
        </div>

        <div class="formitem clear">
            <span class="inputlabel cashflowlabel">@lang_fixed_expenses@</span>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="fixed_expenses" id="fixed_expenses" maxlength="20"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_fixed_expenses_help@
            </span>
        </div>

        <div class="formitem clear">
            <span class="inputlabel cashflowlabel">@lang_time_to_market@</span>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="time_to_market" id="time_to_market" maxlength="2"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_time_to_market_help@
            </span>
        </div>

        <div class="formitem clear">
            <span class="inputlabel cashflowlabel">@lang_gross_margin@</span>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="gross_margin" id="gross_margin" maxlength="3"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_gross_margin_help@
            </span>
        </div>
        
        <div class="formitem clear">
            <span class="inputlabel cashflowlabel">@lang_growth_rate@</span>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="growth_rate" id="growth_rate" maxlength="4"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_growth_rate_help@
            </span>
       </div>
        
        <div class="formitem clear">
            <div id="table_div" class="cashflow-table"></div>
        </div>

        <div class="formitem clear">
            <div id="chart_div" class="cashflow-chart"></div>
        </div>

    </div> <!-- end cashflow panel -->

    <div>
        <div class="formitem clear">
            <span class="newlistingmsgsmall" id="newlistingmsg">&nbsp;</span>
        </div>
    </div>

</div> <!-- end span-24 -->
'

