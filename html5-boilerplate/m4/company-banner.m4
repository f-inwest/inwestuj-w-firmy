define(`companybannermacro', `
<div class="companyheader">

    <div class="companyheaderboatwrapper">
        <div class="companyheaderboat"></div>
    </div>
    <div class="container companybannercontainer">
        <div class="companybanner span-24">

            <div class="span-24 preloader preloadercompanybanner">
                <div class="preloaderfloater"></div>
                <div class="preloadericon"></div>
            </div>

            <div class="initialhidden companybannerwrapper">

                <div class="companybannerfold"></div>

                <div class="companybannerlogo tileimg noimage" id="companylogo"></div>

                <div class="companybannercentercol">
                    <p class="companybannertitle" id="title"></p>
                    <div class="companybannertextgrey companybannermantra2" id="mantra"></div>
                    <div class="companybannertextgrey" id="listing_financial_text" class="inputfield"></div>
                    <div id="admintext"></div>
                    <div class="companybannertextgrey">
                        @lang_author@: <span id="profile_link_text"></span>
                    </div>
                    <div class="companybannertextgrey">
                        @lang_category@: <span id="category_link_text"></span>
                    </div>
                    <div class="companybannertextgrey">
                        @lang_website@:
                        <a class="companybannertextlink" href="#" target="_blank" id="websitelink">
                            <span id="domainname" class="companybannertextlink"></span>
                        </a>
                    </div>
                </div>

                <div class="companybannerfollow">
                    <div class="companybannerfollowbtn hoverlink initialhidden" id="followbtn"></div>
                    <div class="inputmsg inputfield last companybannerfollowmsg" id="followmsg"></div>
                </div>

                <div class="companybannerfollow">
                    <div class="companybannersubmitbtn span-4 smallinputbutton initialhidden" id="submitbutton">@lang_submit_project@ &gt;&gt;</div>
                    <div class="inputmsg last companybannerfollowmsg clear" id="submiterrormsg"></div>
                </div>
    
                <div class="companybannerlinks">
                    <div>@lang_location@: <span id="address_link_text"></span></div>
                    <a class="companybannertextlink hoverlink initialhidden" style="display:block;" href="" id="sendmessagelink">@lang_send_message@</a>
                </div>
            </div>

            <div class="header-content header-initial company-menu" id="companynavcontainer">
                <a class="hoverlink companynavlink" href="#" id="basicstab">
                    <div class="company-menu-link hoverlink $1">@lang_basics@</div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="valuationtab">
                    <div class="company-menu-link hoverlink $2">@lang_valuation@</div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="cashflowtab">
                    <div class="company-menu-link hoverlink $3">@lang_cashflow@</div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="modeltab">
                    <div class="company-menu-link hoverlink $4">@lang_model@</div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="presentationtab">
                    <div class="company-menu-link hoverlink $5">@lang_presentation@</div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="bidstab">
                    <div class="company-menu-link hoverlink $6">@lang_investments@<!-- <span id="num_bids"></span> --></div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="commentstab">
                    <div class="company-menu-link hoverlink $7">@lang_comments@<!-- <span id="num_comments"></span> --></div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="questionstab">
                    <div class="company-menu-link hoverlink $8">@lang_questions@<!-- <span id="num_qandas"></span> --></div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="memberstab">
                    <div class="company-menu-link hoverlink $9">@lang_members@</div>
                </a>
            </div>
        </div>
    </div>
</div>
')
