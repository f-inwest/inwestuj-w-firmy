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

<div class="container wrapper">  <!-- initialhidden -->

    <div class="span-16 basicsleft">

        <div class="header-content header-boxpanel-initial">@lang_add_contribution@</div>
        <div class="boxpanel" id="contributionlistparent">
            <div class="contributionaddline" id="addcontributionbox">
                <textarea class="textarea contributiontextarea"
                    id="addcontributionnotes" name="addcontributionnotes" cols="20" rows="5">@lang_bid_notes@</textarea>
                <p class="addcontributionmsg" id="contributionmsg"></p>
                <input id="addcontributionhours" name="addcontributionhours" value="hours" class="text addcontributionhoursinput"></input>
                <input id="addcontributionamount" name="addcontributionamount" value="amount" class="text addcontributionamountinput"></input>
                <span class="span-3 inputbutton messagebutton contributionaddbutton" id="addcontributionbtn">@lang_add@</span>
                <div class="addcontributionspinner preloadericon initialhidden" id="addcommentspinner"></div>
            </div>
        </div>

        <div class="header-content header-boxpanel-initial">@lang_contributions@</div>
        <div class="boxpanel" id="contributionlistparent">
            <div class="" id="contributionslistparent">
                CONTRIBUTIONS
            </div>
        </div>

    </div>

    <div class="company-side-menu-container span-8 last">

        <div class="company-side-menu-inner">
            <div class="header-content header-title-sidebox">@lang_add_member@</div>
            <div id="addmemberbox" class="addmemberbox">
                <input class="text addmemberinput" id="addmembertext" name="addmembertext" value="@lang_member_username@"></input>
                <p class="commenttext" id="membermsg"></p>
                <div class="addmemberspinner preloadericon initialhidden" id="addmemberspinner"></div>
                <a class="sidebox company-menu-sidebox investbutton" style="display: block;" href="#" id="addmemberbtn">@lang_add@</a>
            </div>
        </div>

        <div class="company-side-menu-inner">
            <div class="header-content header-title-sidebox">@lang_members@</div>
            <div class="" id="memberslistparent">
                MEMBERS
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
<script src="js/modules/projectmembers.js"></script>
<script>
(new ValuationPageClass()).load();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
