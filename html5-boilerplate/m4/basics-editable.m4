`
<div class="span-16">

    <div class="boxtitle basicstitle header-nobottom">
        <span class="titletext">@lang_project_information@</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">@lang_instructions@</label>
            <p>@lang_instructions_desc@</p>
            <p>@lang_instructions_warn@</p>
        </div>
        <span class="bmctitlemsg" id="newlistingbasicsmsg"></span>
    </div>

    <div class="boxpanel newlistingpanel">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel inputlabel-name" for="title">@lang_name@</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="title" id="title" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_name@</label>
                <br />
                @lang_project_name_desc@
            </p>
            <span class="inputicon">
                <div id="titleicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="type">@lang_type@</label>
            <span class="inputfield">
                <select id="type" class="text inputwidetext categoryselect">
                    <option value="application">Application</option>
                    <option value="company" selected="selected">Company</option>
                </select>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_type@</label>
                <br />
                @lang_type_desc@
            </p>
            <span class="inputicon">
                <div id="typeicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="platform">@lang_platform@</label>
            <span class="inputfield">
                <select id="platform" class="text inputwidetext categoryselect">
                    <option value="ios">@lang_ios@</option>
                    <option value="android">@lang_android@</option>
                    <option value="windows_phone">@lang_windows_phone@</option>
                    <option value="desktop">@lang_desktop@</option>
                    <option value="website">@lang_web@</option>
                    <option value="other" selected="selected">@lang_other@</option>
                </select>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_platform@</label>
                <br />
                @lang_platform_desc@
            </p>
            <span class="inputicon">
                <div id="platformicon"></div>
            </span>
        </div>

        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="category">@lang_category@</label>
            <span class="inputfield">
                <select id="category" class="text inputwidetext categoryselect">
                </select>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_category@</label>
                <br />
                @lang_project_category_desc@
            </p>
            <span class="inputicon">
                <div id="categoryicon"></div>
            </span>
        </div>

        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="stage">@lang_project_stage@</label>
            <span class="inputfield">
                <select id="stage" class="text inputwidetext categoryselect">
                    <option value="concept">@lang_concept@</option>
                    <option value="startup" selected="selected">@lang_startup@</option>
                    <option value="established">@lang_established@</option>
                </select>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_project_stage@</label>
                <br />
                @lang_project_stage_desc@
            </p>
            <span class="inputicon">
                <div id="stageicon"></div>
            </span>
        </div>

        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="currency">@lang_currency@</label>
            <span class="inputfield">
                <select id="currency" class="text inputwidetext categoryselect">
                    <option value="pln" selected="selected">@lang_currency_pln@</option>
                    <option value="usd">@lang_currency_usd@</option>
                </select>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_project_currency@</label>
                <br />
                @lang_project_currency_desc@
            </p>
            <span class="inputicon">
                <div id="currencyicon"></div>
            </span>
        </div>

<!--
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="contact_email">EMAIL</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="contact_email" id="contact_email" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Email</label>
                <br />
                Email address where you want to be contacted regarding this listing.
            </p>
            <span class="inputicon">
                <div id="contact_emailicon"></div>
            </span>
        </div>
-->
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel inputlabel-mantra" for="mantra">@lang_mantra@</label>
            <span class="inputfield">
                <textarea class="inputtextareatwoline inputwidetext" cols="20" rows="5" name="mantra" id="mantra" maxlength="140"></textarea>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_mantra@</label>
                <br />
                @lang_mantra_desc@
            </p>
            <span class="inputicon">
                <div id="mantraicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel inputlabel-summary" for="summary">@lang_summary@</label>
            <span class="inputfield">
                <textarea class="inputtextareatwoline inputwidetext inputelevatorpitch" cols="20" rows="5" name="summary" id="summary" maxlength="2000"></textarea>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_summary@</label>
                <br />
                @lang_summary_desc@
            </p>
            <span class="inputicon">
                <div id="summaryicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
        </div>
    </div>

    <div class="boxtitle basicstitle header-nobottom">
        <span class="titletext">@lang_logo_title@</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">@lang_logo_title@</label>
            <p>@lang_logo_desc@</p>
        </div>
    </div>

   <div class="boxpanel logopanel">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="address">@lang_logo@</label>
            <span class="inputfield">
                <div class="tileimg noimage" id="logoimg"></div>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_logo_title@</label>
                <br />
                @lang_logo_desc@
            </p>
            <span class="inputicon">
                <div id="logoicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
        </div>
    </div>

'
include(images-panel-editable.m4)
`

    <div class="boxtitle basicstitle header-nobottom">
        <span class="titletext">@lang_location_title@</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">@lang_location_title@</label>
            <p>@lang_location_desc@</p>
        </div>
        <span class="bmctitlemsg" id="newlistingbasicsmsg"></span>
    </div>
    <div class="boxpanel newlistingpanel newlistingpanel-location">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel inputlabel-location" for="address">@lang_location@</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="address" id="address" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_location@</label>
                <br />
                @lang_project_location_desc@
            </p>
            <span class="inputicon">
                <div id="locationicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
        </div>
    </div>

    <div class="formitem clear">
        <span class="inputmsg" id="submiterrormsg"></span>
    </div>

</div> <!-- end left column -->
'

