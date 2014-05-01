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
                <span class="span-3 inputbutton messagebutton contributionaddbutton" id="downloadcontributionbtn">@lang_download@</span>
                <div class="disablecontributionspinner preloadericon initialhidden" id="disablecontributionspinner"></div>
                <div class="addcontributionspinner preloadericon initialhidden" id="downloadcontributionspinner"></div>
            </div>
            <div id="totalcontributionslist"></div>
        </div>

        <div class="header-content header-boxpanel-initial">@lang_add_contribution@</div>
        <div class="boxpanel" id="contributionlistparent">
            <div class="contributionaddline" id="addcontributionbox">
                <textarea class="textarea contributiontextarea"
                    id="addcontributionnotes" name="addcontributionnotes" cols="20" rows="5">@lang_bid_notes@</textarea>
                <p class="addcontributionmsg" id="contributionmsg"></p>
                <input id="addcontributiondate" name="addcontributiondate" value="@lang_date@" class="text addcontributiondateinput"></input>
                <input id="addcontributionhours" name="addcontributionhours" value="@lang_hours@" class="text addcontributionhoursinput"></input>
                <input id="addcontributionamount" name="addcontributionamount" value="@lang_amount@" class="text addcontributionamountinput"></input>
                <span class="span-3 inputbutton messagebutton contributionaddbutton" id="addcontributionbtn">@lang_add@</span>
                <div class="addcontributionspinner preloadericon initialhidden" id="addcommentspinner"></div>
            </div>
        </div>

        <div class="header-content header-boxpanel-initial">@lang_contributions_pending@</div>
        <div class="boxpanel" id="contributionlistparent">
            <div class="" id="contributionslistparent">
                @lang_pending_contributions_notice@
            </div>
            <table>
                <tbody>
                    <tr><th>Date</th><th>Hours</th><th>Amount</th><th>Notes</th><th>Action</th></tr>
                    <tr>
                        <td>4/12/2014</td><td>3.5</td><td>$0.00</td><td>Implemented contribution lists tshaeus stahoeu sat sssttesat u aesotuhast s aoe</td>
                        <td class="approve-cell">
                            <div class="approve-button"></div>
                            <div class="reject-button"></div>
                        </td>
                    </tr>
                </tbody>
            </table>
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
            <!--
            <table>
                <tbody>
                <tr><th></th><th></th></tr>
                <tr><td>ackmed42</td><td class="delete-cell"><div class="delete-button"></div>
                </td></tr>
                </tbody>
            </table>
            -->
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
