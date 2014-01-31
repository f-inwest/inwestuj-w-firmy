`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="add-listing-page">
<div id="wrap">
<div id="newlistingmain">
'
include(header.m4)
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- begin banner -->
    <div class="span-24">
        <div class="formitem clear">
            <span class="inputmsg" id="addlistingmsg"></span>
        </div>
    </div>
<!-- end banner -->

<div class="span-24">

    <div class="initialhidden" id="notloggedin">

        <div class="addlistingtitle">
            @lang_innovatorslink@
        </div>

        <div class="addlistingsubtitle">@lang_sign_in_to_add_project@</div>

        <div class="addlistingloginrow">
            <a id="google_login" href="">
                <div class="addlistinglogin headericon headersignin"></div>
            </a>
            <a id="twitter_login" href="">
                <div class="addlistinglogin headericon headertwittersignin"></div>
            </a>
            <a id="fb_login" href="">
                <div class="addlistinglogin headericon headerfbsignin"></div>
            </a>
        </div>
    </div>

    <div class="initialhidden" id="existinglisting">
        <div id="editblock">
            <div class="addlistingtitle">@lang_pending_listing_msg@</div>
            <div class="addlistingbutton investbutton" id="editlisting">@lang_edit_listing@</div>
        </div>
        <div class="addlistingmsg deletemsg attention" id="deletemsg">
            @lang_delete_listing@<br/>
            @lang_are_you_sure@<br/>
            @lang_undone_warning@
        </div>
        <div class="addlistingbutton investbutton" id="deletebtn">@lang_delete@</div>
        <div class="addlistingbutton investbutton initialhidden" id="deletecancelbtn">@lang_cancel@</div>
    </div>

    <div class="initialhidden welcometext" id="newlisting">

        <a class="new-addlistingbuttonlink" href="/new-listing-basics-page.html">
            <span>@lang_create_project@</span>
        </a>

        <div class="addlistingtitle">@lang_or_import_project_from@</div>

        <div class="addlistingbuttonrow">
            <a class="investbutton new-addlisting-import" href="/import-listing-page.html?type=Angelco">
                AngelList
            </a>
            <a class="investbutton new-addlisting-import" href="/import-listing-page.html?type=AppStore">
                App Store
            </a>
            <a class="investbutton new-addlisting-import" href="/import-listing-page.html?type=ChromeWebStore">
                Chrome Web Store
            </a>
            <a class="investbutton new-addlisting-import" href="/import-listing-page.html?type=CrunchBase">
                CrunchBase
            </a>
            <a class="investbutton new-addlisting-import" href="/import-listing-page.html?type=GooglePlay">
                Google Play
            </a>
            <a class="investbutton new-addlisting-import" href="/import-listing-page.html?type=Startuply">
                Startuply
            </a>
            <a class="investbutton new-addlisting-import" href="/import-listing-page.html?type=WindowsMarketplace">
                Windows Marketplace
            </a>
    </div>

</div> <!-- end span-24 -->
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script>
(new AddListingClass()).load();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
