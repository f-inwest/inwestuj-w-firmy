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

    <div class="formitem clear">
        <label class="inputlabel cashflowlabel" for="cashflow_type">@lang_cashflow_type@</label>
        <span class="inputfield cashflowfield">
            <select id="cashflow_type" class="text askinginputtext cashflowtypeselect">
                <option value="company">@lang_cashflow_type_company@&nbsp;</option>
                <option value="application">@lang_cashflow_type_app@&nbsp;</option>
            </select>
        </span>
        </p>
        <span class="cashflowhelptext">
                @lang_cashflow_type_help@
        </span>
    </div>

</div> 
'
include(cashflow-editable-company-panel.m4)
include(cashflow-editable-application-panel.m4)
`    

<div>
    <div class="formitem clear">
        <span class="newlistingmsgsmall" id="newlistingmsg">&nbsp;</span>
    </div>
</div>

</div> <!-- end span-24 -->
'

