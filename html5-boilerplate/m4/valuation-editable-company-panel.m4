`
<div class="valuationpanel initialhidden" id="valuation_company_wrapper">

    <div class="formitem clear firstinputitem">
        <p class="formhelptext">
            @lang_company_valuation_intro@
        </p>
    </div>

    <div class="formitem clear">
        <label class="inputlabel valuationlabel" for="development_stage">@lang_development_stage@</label>
        <span class="inputfield valuationfield">
            <select id="development_stage" class="text askinginputtext valuationinput developmentstageselect">
                <option value="concept" selected="selected">@lang_concept@&nbsp;</option>
                <option value="team">@lang_team_in_place@&nbsp;</option>
                <option value="product">@lang_product_ready@&nbsp;</option>
                <option value="customers">@lang_customer_gains@&nbsp;</option>
                <option value="profitability">@lang_growing_profit@&nbsp;</option>
            </select>
        </span>
        <span class="valuationhelptext">
            @lang_development_stage_help@
        </span>
    </div>

    <div class="formitem clear">
        <span class="inputlabel valuationlabel">@lang_cost_to_duplicate@</span>
        <span class="inputfield valuationfield">
            <input class="text askinginputtext valuationinput" type="text" name="cost_to_duplicate" id="cost_to_duplicate" maxlength="20"></input>
        </span>
        <span class="valuationhelptext">
            @lang_cost_to_duplicate_help@
        </span>
    </div>

    <div class="formitem clear">
        <span class="inputlabel valuationlabel">@lang_current_revenue@</span>
        <span class="inputfield valuationfield">
            <input class="text askinginputtext valuationinput" type="text" name="current_revenue" id="current_revenue" maxlength="20"></input>
        </span>
        <span class="valuationhelptext">
            @lang_current_revenue_help@
        </span>
    </div>

    <div class="formitem clear">
        <label class="inputlabel valuationlabel" for="analyze_company_potential">@lang_analyze_potential@</label>
        <span class="inputfield valuationfield">
            <select id="analyze_company_potential" class="text askinginputtext valuationinput developmentstageselect">
                <option value="true">@lang_yes@&nbsp;</option>
                <option value="false" selected="selected">@lang_no@&nbsp;</option>
            </select>
        </span>
        <span class="valuationhelptext">
            @lang_analyze_potential_help@
        </span>
    </div>

    <div class="initialhidden" id="analyze_company_potential_wrapper">

        <div class="formitem clear">
            <span class="inputlabel valuationlabel">@lang_market_size@</span>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="market_size" id="market_size" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                @lang_market_size_help@
            </span>
        </div>

        <div class="formitem clear">
            <span class="inputlabel valuationlabel">@lang_revenue_per@</span>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="revenue_per" id="revenue_per" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                @lang_revenue_per_help@
            </span>
        </div>
      
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">@lang_exit_value@</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="exit_value"></span>
            </span>
            <span class="valuationhelptext">
                @lang_exit_value_help@
            </span>
        </div>

    </div>

    <div class="formitem clear">
        <span class="inputlabel valuationlabel">@lang_final_valuation@</span>
        <span class="inputfield valuationfield">
            <span class="valuationtext" id="company_valuation"></span>
        </span>
        <span class="valuationhelptext">
            @lang_final_valuation_help@
        </span>
    </div>
    
</div> <!-- end valuation panel -->
'
