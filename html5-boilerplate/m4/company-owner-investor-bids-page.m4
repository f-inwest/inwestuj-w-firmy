`
<!doctype html>
<html lang="@lang_html_language@">
'
include(head.m4)
`
<body class="company-page">
<div id="fb-root"></div> <!-- for fb like button -->
<div id="wrap">
<div id="main">
'
include(header.m4)
include(company-not-found.m4)
include(company-banner.m4)
companybannermacro(`', `', `', `', `', `companynavselected', `', `', `')
`
<div class="container preloader">
     <div class="preloaderfloater"></div>
     <div class="preloadericon"></div>
</div>

<div class="container bidscontainer initialhidden wrapper">

    <div style="height: 110px">
        <a href="#" class="backbuttonlink">
            <span class="push-1 span-6 inputbutton backbutton investorbackbutton">
                &lt;&lt; @lang_bids_back_to_investor@
            </span>
        </a>
    </div>

'
include(company-order-book.m4)
`
<div class="header-content header-boxpanel-initial header-boxpanel-full">@lang_bids_from_investor@ <a id="investor_nickname" href="#"></a>
    <span class="newlistingtitlemsg" id="bidtitlemsg"></span>
</div>
<div class="initialhidden clear" id="bidsloggedin">
    <div class="boxpanel boxpanelfull" id="bidlistparent">
        <div id="bidlistlast"></div>
    </div>

    <div class="boxtitlegap smokegrey clear initialhidden" id="new_bid_boxtitle">@lang_bids_make_counter_offer@
        <span class="newlistingtitlemsg" id="newbidtitlemsg"></span>
    </div>

    <div class="boxpanel boxpanelfull initialhidden" id="new_bid_boxparent">

        <div class="messageline new_bid_line" id="new_bid_box">

            <div class="formitem clear firstinputitem">
                <label class="inputlabel" for="new_bid_amt">&nbsp;</label>
                <span class="inputfield">
                    <div class="span-4 investbutton askingamtbtn">@lang_asking_amt_1@</div>
                    <div class="span-4 investbutton askingamtbtn">@lang_asking_amt_2@</div>
                    <div class="span-4 investbutton askingamtbtn">@lang_asking_amt_3@</div>
                    <div class="span-4 last investbutton askingamtbtn">@lang_asking_amt_4@</div>
                </span>
            </div>
    
            <div class="formitem sideinfoitem clear">
                <span class="inputlabel biglabel">@lang_bids_amount@</span>
                <span class="inputfield">
                    <input class="text askinginputtext" type="text" name="new_bid_amt" id="new_bid_amt" maxlength="20"></input>
                </span>
                <span class="inputicon newbidicon amountbidicon">
                    <div id="new_bid_amticon"></div>
                </span>
                <span class="newbiddesc">
                    <p>
                    @lang_new_bid_desc@
                    </p>
                </span>
            </div>

            <div class="formitem clear">
                <label class="inputlabel" for="new_bid_pct">&nbsp;</label>
                <span class="inputfield">
                    <div class="span-4 investbutton askingpctbtn">5%</div>
                    <div class="span-4 investbutton askingpctbtn">25%</div>
                    <div class="span-4 investbutton askingpctbtn">50%</div>
                    <div class="span-4 last investbutton askingpctbtn">100%</div>
                </span>
            </div>
    
            <div class="formitem sideinfoitem clear">
                <span class="inputlabel biglabel">@lang_bids_for@</span>
                <span class="inputfield inputpctfield">
                    <input class="text askinginputtext" type="text" name="new_bid_pct" id="new_bid_pct" maxlength="4"></input>
                </span>
                <span class="inputpcttext">%
                </span>
                <span class="inputicon newbidicon">
                    <div id="new_bid_pcticon"></div>
                </span>
                <span class="newbiddesc">
                    <p>
                        @lang_newbid_equity_desc@
                    </p>
                </span>
            </div>
    
            <div class="formitem clear">
                <span class="inputlabel newbidvallabel">@lang_valuation_is@</span>
                <span class="inputfield newbidvaluationfield">
                    <div class="financialsvaluationtext" id="new_bid_val"></div>
                </span>
                <span class="newbiddesc">
                    <p>
                    @lang_newbid_val_desc@
                    </p>
                </span>
            </div>
    
            <div class="formitem clear">
                <label class="inputlabel newbidnotelabel" for="note">@lang_bids_note@</label>
                <span class="inputfield">
                    <textarea class="textarea new_bid_textarea" name="note" id="new_bid_text" cols="20" rows="5">@lang_bids_note_for_owner@</textarea>
                </span>
                <span class="inputicon">
                    <div id="new_bid_texticon"></div>
                </span>
            </div>
    
            <div class="newbidactionline" id="newbidbuttons">
                <span class="span-3 inputbutton bidactionbutton initialhidden" id="owner_counter_btn">@lang_bids_counter@</span>
                <span class="span-17 bidconfirmmessage" id="new_bid_msg"></span>
            </div>
            <div class="newbidactionline initialhidden" id="newconfirmbuttons">
                <span class="span-3 inputbutton bidactionbutton" id="owner_new_cancel_btn">@lang_bids_cancel@</span>
                <span class="span-3 inputbutton bidactionbutton" id="owner_new_confirm_btn">@lang_bids_confirm@</span>
                <span class="span-17 bidconfirmmessage" id="owner_new_msg"></span>
            </div>

        </div>

    </div>

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/forms.js"></script>
<script src="js/modules/bids.js"></script>
<script>
(new OwnerSingleInvestorBidListClass()).load();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
