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
    
                <p class="companybannertitle" id="title"></p>
                <div class="companybannertextgrey companybannermantra2" id="mantra"></div>
                <div class="companybannertextgrey">
                    <span id="listing_financial_text" class="inputfield"></span><br/>
                    <a class="companybannertextlink hoverlink initialhidden" href="" id="sendmessagelink">@lang_send_message@</a><br/>
                    <span id="admintext"></span>
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
                    <div>@lang_author@: <span id="profile_link_text"></span></div>
                    <div>@lang_website@:
                        <a class="companybannertextlink" href="#" target="_blank" id="websitelink">
                            <span id="domainname" class="companybannertextlink"></span>
                        </a>
                    </div>
                    <div>@lang_category@: <span id="category_link_text"></span></div>
                    <div>@lang_location@: <span id="address_link_text"></span></div>
                </div>
            </div>

            <div class="header-content header-initial company-menu" id="companynavcontainer">
                <a class="hoverlink" href="#" id="basicstab">
                    <div class="company-menu-link hoverlink $1">@lang_basics@</div>
                </a>
                <a class="hoverlink" href="#" id="valuationtab">
                    <div class="company-menu-link hoverlink $2">@lang_valuation@</div>
                </a>
                <a class="hoverlink" href="#" id="modeltab">
                    <div class="company-menu-link hoverlink $3">@lang_model@</div>
                </a>
                <a class="hoverlink" href="#" id="presentationtab">
                    <div class="company-menu-link hoverlink $4">@lang_presentation@</div>
                </a>
                <a class="hoverlink" href="#" id="bidstab">
                    <div class="company-menu-link hoverlink $5">@lang_investments@<!-- <span id="num_bids"></span> --></div>
                </a>
                <a class="hoverlink" href="#" id="commentstab">
                    <div class="company-menu-link hoverlink $6">@lang_comments@<!-- <span id="num_comments"></span> --></div>
                </a>
                <a class="hoverlink" href="#" id="questionstab">
                    <div class="company-menu-link hoverlink $7">@lang_questions@<!-- <span id="num_qandas"></span> --></div>
                </a>
            </div>
        </div>
    </div>
</div>
')
