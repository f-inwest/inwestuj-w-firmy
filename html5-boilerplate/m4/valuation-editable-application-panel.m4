`
<div class="valuationpanel initialhidden" id="valuation_application_wrapper">

    <div class="formitem clear firstinputitem">
        <p class="formhelptext">
            @lang_application_intro@
        </p>
    </div>
    
    <div class="formitem clear">
        <label class="inputlabel valuationlabel" for="is_app_released">@lang_is_app_released@</label>
        <span class="inputfield valuationfield">
            <select id="is_app_released" class="text askinginputtext valuationinput developmentstageselect">
                <option value="true" selected="selected">@lang_yes@&nbsp;</option>
                <option value="false">@lang_no@&nbsp;</option>
            </select>
        </span>
        <span class="valuationhelptext">
            @lang_is_app_released_help@
        </span>
    </div>

    <div class="initialhidden" id="is_app_released_wrapper">
    
        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="cost_of_app">@lang_cost_of_app@</label>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="cost_of_app" id="cost_of_app" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                @lang_cost_of_app_help@
            </span>
        </div>
    
        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="months_live">@lang_months_live@</label>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="months_live" id="months_live" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                @lang_months_live_help@
            </span>
        </div>
    
        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="best_month">@lang_best_month@</label>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="best_month" id="best_month" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                @lang_best_month_help@
            </span>
        </div>

        <div class="formitem clear">
            <span class="inputlabel valuationlabel">@lang_projected_peak@</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="projected_peak"></span>
            </span>
            <span class="valuationhelptext">
                @lang_projected_peak_help@
            </span>
        </div>
    
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">@lang_future_earnings@</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="future_earnings"></span>
            </span>
            <span class="valuationhelptext">
                @lang_future_earnings_help@
            </span>
        </div>
    
    </div>

    <div class="formitem clear">
        <label class="inputlabel valuationlabel" for="analyze_app_potential">@lang_analyze_potential@</label>
        <span class="inputfield valuationfield">
            <select id="analyze_app_potential" class="text askinginputtext valuationinput developmentstageselect">
                <option value="true">@lang_yes@&nbsp;</option>
                <option value="false" selected="selected">@lang_no@&nbsp;</option>
            </select>
        </span>
        <span class="valuationhelptext">
            @lang_analyze_potential_help@
        </span>
    </div>

    <div class="initialhidden" id="analyze_app_potential_wrapper">

        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="target_users">@lang_target_users@</label>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="target_users" id="target_users" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                @lang_target_users_help@
            </span>
        </div>
     
        <div class="formitem clear">
            <label class="inputlabel valuationlabel" for="monthly_arpu">@lang_monthly_arpu@</label>
            <span class="inputfield valuationfield">
                <input class="text askinginputtext valuationinput" type="text" name="monthly_arpu" id="monthly_arpu" maxlength="20"></input>
            </span>
            <span class="valuationhelptext">
                @lang_monthly_arpu_help@
            </span>
        </div>
     
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">@lang_monthly_target@</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="monthly_target"></span>
            </span>
            <span class="valuationhelptext">
                @lang_monthly_target_help@
            </span>
        </div>
    
        <div class="formitem clear">
            <span class="inputlabel valuationlabel">@lang_target_valuation@</span>
            <span class="inputfield valuationfield">
                <span class="valuationtext" id="target_valuation"></span>
            </span>
            <span class="valuationhelptext">
                @lang_target_valuation_help@
            </span>
        </div>

    </div>
        
    <div class="formitem clear">
        <span class="inputlabel valuationlabel">@lang_final_valuation@</span>
        <span class="inputfield valuationfield">
            <span class="valuationtext" id="application_valuation"></span>
        </span>
        <span class="valuationhelptext">
            @lang_final_valuation_app_help@
        </span>
    </div>
    
</div> <!-- end valuation panel -->
'
