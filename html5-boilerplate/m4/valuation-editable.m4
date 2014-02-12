`
<div class="span-16">
    <div class="header-content" style="margin-bottom: 0; height: 45px;">
        <div class="header-title">@lang_valuation_wizard@</div>
    </div>
</div>

<div class="span-8 last">
    <a href="#" class="backbuttonlink span-3 investbutton backbutton">
        &lt;&lt; @lang_back@
    </a>
</div>

<div class="span-24 valuationwrapper">

<div>

    <div class="formitem clear firstinputitem">
        <p class="formhelptext">
            @lang_valuation_intro@
        </p>
    </div>

    <div class="formitem clear">
        <label class="inputlabel valuationlabel" for="valuation_type">@lang_valuation_type@</label>
        <span class="inputfield valuationfield">
            <select id="valuation_type" class="text askinginputtext valuationtypeselect">
                <option value="company">@lang_valuation_type_company@&nbsp;</option>
                <option value="application">@lang_valuation_type_app@&nbsp;</option>
            </select>
        </span>
        </p>
        <span class="valuationhelptext">
                @lang_valuation_type_help@
        </span>
    </div>

</div> 
'
include(valuation-editable-company-panel.m4)
include(valuation-editable-application-panel.m4)
`    

<div>
    <div class="formitem clear">
        <span class="newlistingmsgsmall" id="newlistingmsg">&nbsp;</span>
    </div>
</div>

</div> <!-- end span-24 -->
'

