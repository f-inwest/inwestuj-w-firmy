`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="new-listing-financials-page">
<div id="wrap">
<div id="newlistingmain">
'
include(header.m4)
include(company-not-found.m4)
`
<div class="banner genericbanner">
    <div class="container">
        <span class="bannertext span-24">
            <div class="welcometitle">@lang_wizard_title@</div>
            <div class="welcometext">@lang_project_funding@</div>
        </span>
    </div>
</div>

<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- begin banner -->
<!-- end banner -->

<div class="span-16 initialhidden" id="newlistingfinancialswrapper">

    <div class="boxtitle basicstitle header-nobottom">
        <span class="titletext">@lang_funding_status@</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">@lang_instructions@</label>
            <p>
            @lang_ask_for_funding_desc@
            </p>
        </div>
    </div>
    <div class="boxpanel">
        <div class="formitem clear firstinputitem">
            <label class="inputlabel valuation-allow-label" for="asked_fund">@lang_allow_bids@</label>
            <span class="inputcheckbox">
                <div id="asked_fund"></div>
            </span>
            <span class="inputhelp inputmsg"><span id="askfundstatus"></span><span class="newlistingaskmsg" id="newlistingaskmsg">&nbsp;</span></span>
        </div>
    </div>

</div>

<div class="span-8 last">
    <a href="#" class="backbuttonlink">
        <span class="push-1 span-3 inputbutton backbutton" style="margin-bottom: 0;">
                &lt;&lt; @lang_back@
        </span>
    </a>
</div>

<div class="span-24">
    <div class="offerwrapper offerwrapperdisplay boxpanelfull header-nobottom" id="offerwrapper">
    <div class="boxtitle offertitle boxpanelfull header-nobottom">
        <span class="titletext">@lang_funding_details@</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">@lang_instructions@</label>
            <p>
                @lang_funding_details_desc@
            </p>
        </div>
    </div>

    <div class="boxpanelfull offerpanel" id="offerpanel">

        <div class="formitem clear firstinputitem">
            <label class="inputlabel valuation-offer-label" for="suggested_amt">@lang_asking@</label>
            <span class="inputfield">
                <div class="span-4 investbutton askingamtbtn">@lang_asking_amt_1@</div>
                <div class="span-4 investbutton askingamtbtn">@lang_asking_amt_2@</div>
                <div class="span-4 investbutton askingamtbtn">@lang_asking_amt_3@</div>
                <div class="span-4 investbutton askingamtbtn">@lang_asking_amt_4@</div>
                <!--
                <div class="span-4 investbutton askingamtbtn">@lang_asking_amt_1@</div>
                -->
            </span>
        </div>

        <div class="formitem sideinfoitem clear">
            <span class="inputlabel">&nbsp;</span>
            <span class="inputfield">
                <input class="text askinginputtext" type="text" name="suggested_amt" id="suggested_amt" maxlength="20"></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_instructions@</label>
                <br/>
                @lang_asking_desc@
            </p>
            <span class="inputicon">
                <div id="suggested_amticon"></div>
            </span>
        </div>

        <div class="formitem clear">
            <label class="inputlabel valuation-offer-label" for="suggested_pct">@lang_offer_for@</label>
            <span class="inputfield">
                <div class="span-4 investbutton askingpctbtn">5%</div>
                <div class="span-4 investbutton askingpctbtn">25%</div>
                <div class="span-4 investbutton askingpctbtn">50%</div>
                <div class="span-4 investbutton askingpctbtn">100%</div>
            </span>
        </div>

        <div class="formitem sideinfoitem clear">
            <span class="inputlabel">&nbsp;</span>
            <span class="inputfield">
                <input class="text askinginputtext" type="text" name="suggested_pct" id="suggested_pct" maxlength="4"></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_instructions@</label>
                <br/>
                @lang_for_desc@
            </p>
            <span class="inputicon">
                <div id="suggested_pcticon"></div>
            </span>
        </div>

        <div class="formitem clear">
            <span class="inputlabel valuation-offer-label">@lang_valuation@</span>
            <span class="inputfield financialsvaluationfield">
                <div class="financialsvaluationtext" id="suggested_val"></div>
            </span>
            <div class="valuation-offer-desc">@lang_newbid_val_desc@</div>
        </div>

        <div class="formitem clear">
            <span class="inputlabel">&nbsp;</span>
            <span class="inputfield">
                <a href="/new-listing-valuation-page.html">
                    <div class="span-8 investbutton">@lang_valuation_help@</div>
                </a>
            </span>
        </div>

        <div class="formitem clear">
            <span class="newlistingmsgsmall newlistingoffermsg" id="newlistingoffermsg">&nbsp;</span>
        </div>

    </div>

    </div>
<!--
    <div class="boxtitle offertitle">
        <span class="titletext">OWNERSHIP</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Ownership</label>
            <p>
            Who owns your company or application?  List all applicable owners including founders, employees, corporations and investors, if any.
            </p>
        </div>
    </div>
    <div class="boxpanel newlistingpanel">
        <div class="formitem sideinfoitem clear firstinputitem">
            <label class="inputlabel" for="founders">OWNERS</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="founders" id="founders" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Owners</label>
                <br/>
                The full legal names of all individual authors of this application or owners of the company.
            </p>
            <span class="inputicon">
                <div id="foundersicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="newlistingmsgsmall" id="newlistingfoundersmsg">&nbsp;</span>
        </div>
    </div>
-->
    <div>
        <div class="formitem clear">
            <span class="newlistingmsgsmall" id="newlistingmsg">&nbsp;</span>
        </div>
    </div>

</div>
'
include(new-listing-bottom-buttons.m4)
`

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/forms.js"></script>
<script src="js/modules/newlistingbase.js"></script>
<script src="js/modules/newlistingfinancialspage.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
