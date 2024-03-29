`
<!-- begin basics wrapper -->
<div class="basicswrapper" id="basicswrapper">
    <!-- preview overlay click disabler -->
    <div class="previewoverlay initialhidden" id="previewoverlay">
        <div class="previewtext">@lang_preview_upper@</div>
        <div class="previewtext previewtext2">@lang_preview_upper@</div>
    </div>

    <div class="span-16 basicsleft">
        <div class="header-content header-boxpanel-initial">@lang_summary@</div>
        <div class="boxpanel summarypanel">
            <p class="indentedtext" id="summary"></p>
    	</div>

'
include(images-panel.m4)
`
        <div class="initialhidden" id="videowrapper">
            <div class="header-content header-boxpanel-initial header-video">
                @lang_video@
                <a class="videolink videolink-top" href="#" id="videolink">@lang_view_on_youtube@</a>
            </div>
            <div class="boxpanel videopanel">
        	    <div class="videocontainer">
              	    <iframe width="627" height="353" id="videopresentation" src="" frameborder="0" allowfullscreen></iframe>
        	    </div>
            </div>
        </div>

    </div>

    <div class="company-side-menu-container span-8 last">

        <div class="company-side-menu-inner company-side-menu-inner">
        	<div class="header-content header-title-sidebox header-title-sidebox-buttonbox">@lang_project_data@</div>
        	<a class="sidebox company-menu-sidebox investbutton initialhidden" style="display: block;" href="#" id="basicsbutton">@lang_basics@</a>
            <a class="sidebox company-menu-sidebox investbutton initialhidden blueboxbutton firstsidebutton" style="display: block;" href="#" id="investbutton">@lang_invest@</a>
            <div class="sidebox company-menu-sidebox investbutton" id="valuationbutton">@lang_valuation@</div>
            <div class="sidebox company-menu-sidebox investbutton" id="cashflowbutton">@lang_cashflow@</div>
            <div class="sidebox company-menu-sidebox investbutton" id="modelbutton">@lang_model@</div>
            <div class="sidebox company-menu-sidebox investbutton" id="presentationbutton">@lang_presentation@</div>
            <div class="sidebox company-menu-sidebox investbutton" id="contributionsbutton">@lang_members@</div>
        </div>

        <div class="initialhidden company-side-menu-inner company-side-menu-inner-pricepoints" id="pricepoints-wrapper">
            <div class="header-content header-title-sidebox">@lang_purchases@</div>
            <div class="" id="pricepoints-wrapper-inner">
            </div>
        </div>

        <div class="company-side-menu-inner">
            <div class="header-content header-title-sidebox">@lang_funding_status@</div>
            <div class="sidebox company-menu-sidebox uneditabletext askingbox">
                <div class="sideboxdesc suggestedmsg" id="suggestedmsg"></div>
                <div class="suggestedinfo" id="suggestedinfo">
                    <div class="sideboxtitlecompact sideboxnum stan-osoby" id="suggested_amt"></div>
                    <div class="sideboxdesc stan-projektu">@lang_for_equity@ <span class="sideboxnum" id="suggested_pct"></span><span class="sideboxnum">%</span> @lang_equity@</div>
                    <div class="sideboxdesc stan-projektu">@lang_valuation_is@ <span class="sideboxnum" id="suggested_val"></span></div>
    <!--                <div class="sideboxdesc stan-projektu">Total raised is <span class="sideboxnum" id="total_raised">$0</span></div> -->
                </div>
            </div>

            <a href="/new-listing-financials-page.html">
                <div class="sidebox company-menu-sidebox investbutton initialhidden" id="fundingbutton">@lang_ask_for_funding@</div>
            </a>
        </div>

        <div class="company-side-menu-inner">
            <div class="header-content header-title-sidebox">@lang_documents@</div>
            <div class="initialhidden" id="documentboxwrapper">
            <div class="sidebox company-menu-sidebox documentbox" id="documentbox">
                    <div class="downloadline hoverlink initialhidden" id="presentationwrapper">
                        <a href="#" id="presentationlink">
                            <div class="downloadicon"></div>
                            <div class="downloadtext">@lang_download_presentation@</div>
                        </a>
                    </div>
                    <div class="downloadline hoverlink initialhidden" id="businessplanwrapper">
                        <a href="#" id="businessplanlink">
                            <div class="downloadicon"></div>
                            <div class="downloadtext">@lang_download_business_plan@</div>
                        </a>
                    </div>
                    <div class="downloadline hoverlink initialhidden" id="financialswrapper">
                        <a href="#" id="financialslink">
                            <div class="downloadicon"></div>
                            <div class="downloadtext">@lang_download_financials@</div>
                        </a>
                    </div>
            </div>
            </div>

            <div class="sidebox company-menu-sidebox investbutton initialhidden" style="margin-top: 15px" id="adddocumentbutton">@lang_add_document@</div>
            <div class="sidebox company-menu-sidebox investbutton initialhidden" id="requestpresentationbutton">@lang_request_powerpoint@</div>
            <div class="sidebox company-menu-sidebox investbutton initialhidden" id="requestbusinessplanbutton">@lang_request_plan@</div>
            <div class="sidebox company-menu-sidebox investbutton initialhidden" id="requestfinancialsbutton">@lang_request_statements@</div>
        </div>

        <div class="company-side-menu-inner">
            <div class="header-content header-title-sidebox">@lang_location@</div>
            <div class="sidebox company-menu-sidebox ">
                <div class="addresstext" id="fulladdress"></div>
                <div class="sideboxmap">
                    <a href="#" class="formlink hoverlink" target="_blank" id="addresslink">
                        <img src="#" id="mapimg"></img>
                    </a>
                </div>
            </div>
        </div>

        <div class="company-side-menu-inner">
            <div class="header-content header-title-sidebox">@lang_share@</div>
            <div class="sidebox socialsidebox company-menu-sidebox" id="socialsidebox">

                <div class="twitterbanner" id="twitterbanner">
                    <a href="https://twitter.com/share" class="twitter-share-button" data-via="inwestuj-w-firmy">@lang_tweet@</a>
                </div>
                <div class="facebookbanner" id="facebookbanner"></div>
                <div class="gplusbanner" id="gplusbanner"></div>
            </div>
        </div>

        <div class="company-side-menu-inner company-side-menu-inner-maybe-empty">
            <div id="deletebox" class="deletebox">
            <div class="deletemsg attention" id="deletemsg">@lang_are_you_sure@<br/>@lang_undone_warning@</div>
            <a href="#" id="deletebtn"><div class="sideboxbutton company-menu-sidebox hoverlink">@lang_delete@</div></a>
            <a href="#" class="deletecancelbtn" id="deletecancelbtn"><div class="sideboxbutton company-menu-sidebox hoverlink">@lang_cancel@</div></a>
            </div>

            <div id="withdrawbox" class="withdrawbox">
            <div class="withdrawmsg attention" id="withdrawmsg">@lang_are_you_sure@<br/>@lang_undone_warning@</div>
            <a href="#" id="withdrawbtn"><div class="sideboxbutton company-menu-sidebox hoverlink">@lang_withdraw@</div></a>
            <a href="#" class="withdrawcancelbtn" id="withdrawcancelbtn"><div class="sideboxbutton company-menu-sidebox hoverlink">@lang_cancel@</div></a>
            </div>

            <div id="approvebox" class="approvebox">
            <div class="approvemsg attention" id="approvemsg">@lang_are_you_sure@</div>
            <a href="#" id="approvebtn"><div class="sideboxbutton company-menu-sidebox hoverlink">@lang_activate_project@</div></a>
            <a href="#" class="approvecancelbtn" id="approvecancelbtn"><div class="sideboxbutton company-menu-sidebox hoverlink">@lang_cancel@</div></a>
            </div>

            <div id="sendbackbox" class="sendbackbox">
            <input class="text sideinputtext" type="text" name="sendbacktext" id="sendbacktext" value="@lang_send_back_reason@" length="35" maxlength="100"></input>
            <div class="sendbackmsg attention" id="sendbackmsg">@lang_are_you_sure@</div>
            <a href="#" id="sendbackbtn"><div class="sideboxbutton company-menu-sidebox hoverlink">@lang_send_back@</div></a>
            <a href="#" class="sendbackcancelbtn" id="sendbackcancelbtn"><div class="sideboxbutton company-menu-sidebox hoverlink">@lang_cancel@</div></a>
            </div>

            <div id="freezebox" class="freezebox">
            <input class="text sideinputtext" type="text" name="freezetext" id="freezetext" value="" length="35" maxlength="100"></input>
            <div class="freezemsg attention" id="freezemsg">@lang_are_you_sure@</div>
            <a href="#" id="freezebtn"><div class="sideboxbutton company-menu-sidebox hoverlink">@lang_freeze@</div></a>
            <a href="#" class="freezecancelbtn" id="freezecancelbtn"><div class="sideboxbutton company-menu-sidebox hoverlink">@lang_cancel@</div></a>
            </div>
        </div>

    </div>

</div> <!-- end basics wrapper -->
'
