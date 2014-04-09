`
<div class="cashflowpanel initialhidden" id="cashflow_company_wrapper">

    <div class="formitem clear firstinputitem">
        <p class="formhelptext">
            @lang_company_cashflow_intro@
        </p>
    </div>

    <div class="formitem clear">
        <label class="inputlabel cashflowlabel" for="development_stage">@lang_development_stage@</label>
        <span class="inputfield cashflowfield">
            <select id="development_stage" class="text askinginputtext cashflowinput developmentstageselect">
                <option value="concept" selected="selected">@lang_concept@&nbsp;</option>
                <option value="team">@lang_team_in_place@&nbsp;</option>
                <option value="product">@lang_product_ready@&nbsp;</option>
                <option value="customers">@lang_customer_gains@&nbsp;</option>
                <option value="profitability">@lang_growing_profit@&nbsp;</option>
            </select>
        </span>
        <span class="cashflowhelptext">
            @lang_development_stage_help@
        </span>
    </div>

    <div class="formitem clear">
        <span class="inputlabel cashflowlabel">@lang_cost_to_duplicate@</span>
        <span class="inputfield cashflowfield">
            <input class="text askinginputtext cashflowinput" type="text" name="cost_to_duplicate" id="cost_to_duplicate" maxlength="20"></input>
        </span>
        <span class="cashflowhelptext">
            @lang_cost_to_duplicate_help@
        </span>
    </div>

    <div class="formitem clear">
        <span class="inputlabel cashflowlabel">@lang_current_revenue@</span>
        <span class="inputfield cashflowfield">
            <input class="text askinginputtext cashflowinput" type="text" name="current_revenue" id="current_revenue" maxlength="20"></input>
        </span>
        <span class="cashflowhelptext">
            @lang_current_revenue_help@
        </span>
    </div>

    <div class="formitem clear">
        <label class="inputlabel cashflowlabel" for="analyze_company_potential">@lang_analyze_potential@</label>
        <span class="inputfield cashflowfield">
            <select id="analyze_company_potential" class="text askinginputtext cashflowinput developmentstageselect">
                <option value="true">@lang_yes@&nbsp;</option>
                <option value="false" selected="selected">@lang_no@&nbsp;</option>
            </select>
        </span>
        <span class="cashflowhelptext">
            @lang_analyze_potential_help@
        </span>
    </div>

    <div class="initialhidden" id="analyze_company_potential_wrapper">

        <div class="formitem clear">
            <span class="inputlabel cashflowlabel">@lang_market_size@</span>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="market_size" id="market_size" maxlength="20"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_market_size_help@
            </span>
        </div>

        <div class="formitem clear">
            <span class="inputlabel cashflowlabel">@lang_revenue_per@</span>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="revenue_per" id="revenue_per" maxlength="20"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_revenue_per_help@
            </span>
        </div>
      
        <div class="formitem clear">
            <span class="inputlabel cashflowlabel">@lang_exit_value@</span>
            <span class="inputfield cashflowfield">
                <span class="cashflowtext" id="exit_value"></span>
            </span>
            <span class="cashflowhelptext">
                @lang_exit_value_help@
            </span>
        </div>

    </div>

    <div class="formitem clear">
        <span class="inputlabel cashflowlabel">@lang_final_cashflow@</span>
        <span class="inputfield cashflowfield">
            <span class="cashflowtext" id="company_cashflow"></span>
        </span>
        <span class="cashflowhelptext">
            @lang_final_cashflow_help@
        </span>
    </div>
    
</div> <!-- end cashflow panel -->
'
