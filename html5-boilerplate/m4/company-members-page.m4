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
companybannermacro(`', `', `', `', `', `', `', `', `companynavselected')
`
<div class="container preloader">
     <div class="preloaderfloater"></div>
     <div class="preloadericon"></div>
</div>

<div class="container">

    <div class="span-24 basicsleft initialhidden" id="no_contributions_owner">
        <div class="header-content header-boxpanel-initial header-boxpanel-full">@lang_members_contributions@</div>
        <div class="boxpanel boxpanelfull contributionenablepanel">
            <div class="contributionenableline">
                <p class="">@lang_contributions_invite@</p>
                <p class="addcontributionmsg" id="enablecontributionmsg"></p>
                <span class="span-3 inputbutton messagebutton contributionaddbutton" id="enablecontributionbtn">@lang_enable@</span>
                <div class="addcontributionspinner preloadericon initialhidden" id="enablecontributionspinner"></div>
            </div>
        </div>
    </div>

    <div class="span-24 basicsleft initialhidden" id="no_contributions_member">
        <div class="header-content header-boxpanel-initial header-boxpanel-full">@lang_members_contributions@</div>
        <div class="boxpanel boxpanelfull contributionenablepanel">
            <div class="contributionenableline">
                <p class="">@lang_contributions_nonowner_invite@</p>
            </div>
        </div>
    </div>

    <div class="span-16 basicsleft initialhidden" id="contributions_wrapper">

        <div class="header-content header-boxpanel-initial">@lang_contributions_summary@</div>
        <div class="boxpanel contributiondownloadpanel">
            <div class="contributiondownloadline" id="addcontributionbox">
                <p class="contributioninnerp">@lang_contributions_notice@</p>
                <p class="addcontributionmsg contributioninnerp" id="disablecontributionmsg"></p>
                <span class="span-3 inputbutton messagebutton contributiondisablebutton" id="disablecontributionbtn">@lang_disable@</span>
                <span class="span-3 inputbutton messagebutton contributiondownloadbutton" id="downloadcontributionbtn">@lang_download@</span>
                <span class="span-3 inputbutton messagebutton contributionaddbutton initialhidden" id="savecontributionbtn">@lang_save@</span>
                <div class="hourly-rate-label initialhidden">@lang_hourly_rate_title@</div>
                <input class="text hourly-rate-input initialhidden" name="hourlyrate" id="hourlyrate"></input>
                <div class="interest-rate-label initialhidden">@lang_interest_rate_title@</div>
                <input class="text interest-rate-input initialhidden" name="interestrate" id="interestrate"></input>
                <div class="disablecontributionspinner preloadericon initialhidden" id="disablecontributionspinner"></div>
                <div class="downloadcontributionspinner preloadericon initialhidden" id="downloadcontributionspinner"></div>
                <div class="addcontributionspinner preloadericon initialhidden" id="savecontributionspinner"></div>
            </div>
            <div class="initialhidden"><small>@lang_contributions_money_interest_note@</small></div>
            <div id="totalcontributionslist"></div>
        </div>

        <div class="header-content header-boxpanel-initial">@lang_add_contribution@</div>
        <div class="boxpanel" id="contributionlistparent">
            <div class="contributionaddline" id="addcontributionbox">
                <textarea class="textarea contributiontextarea"
                    id="addcontributionnotes" name="addcontributionnotes" cols="20" rows="5">@lang_contrib_notes@</textarea>
                <p class="addcontributionmsg successful" id="addcontributionmsg"></p>
                <input id="addcontributiondate" name="addcontributiondate" value="@lang_date@" class="text addcontributiondateinput"></input>
                <input id="addcontributionhours" name="addcontributionhours" value="@lang_hours@" class="text addcontributionhoursinput"></input>
                <input id="addcontributionamount" name="addcontributionamount" value="@lang_amount@" class="text addcontributionamountinput"></input>
                <span class="span-3 inputbutton messagebutton contributionaddbutton" id="addcontributionbtn">@lang_add@</span>
                <div class="addcontributionspinner preloadericon initialhidden" id="addcontributionspinner"></div>
            </div>
        </div>

        <div class="header-content header-boxpanel-initial initialhidden" id="submittedcontributionstitle">@lang_contributions_pending@</div>
        <div class="boxpanel" id="submittedcontributionswrapper">
            <div id="submittedcontributionsnotice"></div>
            <p class="addcontributionmsg successful" id="approvecontributionmsg"></p>
            <div id="submittedcontributionslist"></div>
        </div>

    </div>

    <div class="company-side-menu-container span-8 last initialhidden" id="members_wrapper">

        <div class="company-side-menu-inner initialhidden" id="add_member_wrapper">
            <div class="header-content header-title-sidebox">@lang_add_member@</div>
            <div id="addmemberbox" class="addmemberbox">
                <div id="addmemberauto" class="addmemberauto"></div>
                <div class="addmemberspinner preloadericon initialhidden" id="addmemberspinner"></div>
                <p class="commenttext errorcolor" id="addmembermsg"></p>
                <a class="sidebox company-menu-sidebox investbutton" style="display: block;" href="#" id="addmemberbtn">@lang_add@</a>
            </div>
        </div>

        <div class="company-side-menu-inner">
            <div class="header-content header-title-sidebox">@lang_members@</div>
            <p class="commenttext errorcolor" id="deletemembermsg"></p>
            <div id="memberslist"></div>
        </div>

    </div>

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/complete.ly.1.0.1.min.js"></script>
<script src="js/modules/projectmembers.js"></script>
<script>
(new MemberPageClass()).load();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
