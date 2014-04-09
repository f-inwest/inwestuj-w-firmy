`
<div class="cashflowpanel initialhidden" id="cashflow_application_wrapper">

    <div class="formitem clear firstinputitem">
        <p class="formhelptext">
            @lang_application_intro@
        </p>
    </div>
    
    <div class="formitem clear">
        <label class="inputlabel cashflowlabel" for="is_app_released">@lang_is_app_released@</label>
        <span class="inputfield cashflowfield">
            <select id="is_app_released" class="text askinginputtext cashflowinput developmentstageselect">
                <option value="true" selected="selected">@lang_yes@&nbsp;</option>
                <option value="false">@lang_no@&nbsp;</option>
            </select>
        </span>
        <span class="cashflowhelptext">
            @lang_is_app_released_help@
        </span>
    </div>

    <div class="initialhidden" id="is_app_released_wrapper">
    
        <div class="formitem clear">
            <label class="inputlabel cashflowlabel" for="cost_of_app">@lang_cost_of_app@</label>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="cost_of_app" id="cost_of_app" maxlength="20"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_cost_of_app_help@
            </span>
        </div>
    
        <div class="formitem clear">
            <label class="inputlabel cashflowlabel" for="months_live">@lang_months_live@</label>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="months_live" id="months_live" maxlength="20"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_months_live_help@
            </span>
        </div>
    
        <div class="formitem clear">
            <label class="inputlabel cashflowlabel" for="best_month">@lang_best_month@</label>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="best_month" id="best_month" maxlength="20"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_best_month_help@
            </span>
        </div>

        <div class="formitem clear">
            <span class="inputlabel cashflowlabel"></span>
            <span class="inputfield cashflowfield">
                <span class="cashflowtext" id="projected_peak"></span>
            </span>
            <span class="cashflowhelptext">
                @lang_projected_peak_help@
            </span>
        </div>
    
        <div class="formitem clear">
            <span class="inputlabel cashflowlabel"></span>
            <span class="inputfield cashflowfield">
                <span class="cashflowtext" id="future_earnings"></span>
            </span>
            <span class="cashflowhelptext">
                @lang_future_earnings_help@
            </span>
        </div>
    
    </div>

    <div class="formitem clear">
        <label class="inputlabel cashflowlabel" for="analyze_app_potential">@lang_analyze_potential@</label>
        <span class="inputfield cashflowfield">
            <select id="analyze_app_potential" class="text askinginputtext cashflowinput developmentstageselect">
                <option value="true">@lang_yes@&nbsp;</option>
                <option value="false" selected="selected">@lang_no@&nbsp;</option>
            </select>
        </span>
        <span class="cashflowhelptext">
            @lang_analyze_potential_help@
        </span>
    </div>

    <div class="initialhidden" id="analyze_app_potential_wrapper">

        <div class="formitem clear">
            <label class="inputlabel cashflowlabel" for="target_users">@lang_target_users@</label>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="target_users" id="target_users" maxlength="20"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_target_users_help@
            </span>
        </div>
     
        <div class="formitem clear">
            <label class="inputlabel cashflowlabel" for="monthly_arpu">@lang_monthly_arpu@</label>
            <span class="inputfield cashflowfield">
                <input class="text askinginputtext cashflowinput" type="text" name="monthly_arpu" id="monthly_arpu" maxlength="20"></input>
            </span>
            <span class="cashflowhelptext">
                @lang_monthly_arpu_help@
            </span>
        </div>
     
        <div class="formitem clear">
            <span class="inputlabel cashflowlabel">@lang_monthly_target@</span>
            <span class="inputfield cashflowfield">
                <span class="cashflowtext" id="monthly_target"></span>
            </span>
            <span class="cashflowhelptext">
                @lang_monthly_target_help@
            </span>
        </div>
    
        <div class="formitem clear">
            <span class="inputlabel cashflowlabel">@lang_target_cashflow@</span>
            <span class="inputfield cashflowfield">
                <span class="cashflowtext" id="target_cashflow"></span>
            </span>
            <span class="cashflowhelptext">
                @lang_target_cashflow_help@
            </span>
        </div>

    </div>
        
    <div class="formitem clear">
        <span class="inputlabel cashflowlabel">@lang_final_cashflow@</span>
        <span class="inputfield cashflowfield">
            <span class="cashflowtext" id="application_cashflow"></span>
        </span>
        <span class="cashflowhelptext">
            @lang_final_cashflow_app_help@
        </span>
    </div>
    
</div> <!-- end cashflow panel -->
'
