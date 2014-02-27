`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="profile-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="banner contactbanner">
    <div class="container">
        <span class="bannertext span-24">
            <div class="welcometitle">
                <span class="titleyour">@lang_your_profile@</span>
                <span class="titleuser initialhidden>@lang_projects@ <span class="titleusername"></span></span>
            </div>
            <div class="welcometext">
                <span id="username"></span>
            </div>
        </span>
    </div>
</div>
'
include(profile-not-found.m4)
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- left column -->
<div class="span-16">
    <div id="profilemsg"></div>

    <div id="no_listings_wrapper" class="initialhidden">
        <div class="header-content">
            <div class="header-title">
                <span class="titleyour">@lang_your_projects@</span>
                <span class="titleuser">@lang_projects@ <span class="titleusername"></span></span>
            </div>
        </div>
        <div class="boxpanel">
            <div class="indentedtext">
            @lang_no_active_projects@
            <div id="encourageuser">
                <a href="/add-listing-page.html" class="inputmsg hoverlink profilelink">@lang_add_project@</a>
            </div>
            </div>
        </div>
    </div>

    <div id="edited_listing_wrapper" class="initialhidden">
        <div class="header-content">
            <div class="header-title">
                <span class="titleyour">@lang_your_pending_project@</span>
                <span class="titleuser">@lang_pending_project@ <span class="titleusername"></span></span>
            </div>
        </div>
        <div id="edited_listing"></div>
    </div>

    <div id="active_listings_wrapper" class="initialhidden">
        <div class="header-content">
            <div class="header-title">
                <span class="titleyour">@lang_your_active_projects@</span>
                <span class="titleuser">@lang_active_projects@ <span class="titleusername"></span></span>
            </div>
        </div>
        <div id="active_listings"></div>
    </div>

    <div id="admin_posted_listings_wrapper" class="initialhidden">
        <div class="header-content">
            <div class="header-title">
                @lang_projects_awaiting_review@    
            </div>
        </div>
        <div id="admin_posted_listings"></div>
    </div>

    <div id="admin_frozen_listings_wrapper" class="initialhidden">
        <div class="header-content">
            <div class="header-title">
                @lang_frozen_projects@
            </div>
        </div>
        <div id="admin_frozen_listings"></div>
    </div>

    <div id="monitored_listings_wrapper" class="initialhidden">
        <div class="header-content">
            <div class="header-title">
                <span class="titleyour">@lang_your_watched_projects@</span>
                <span class="titleuser">@lang_watched_projects@ <span class="titleusername"></span></span>
            </div>
        </div>
        <div id="monitored_listings"></div>
    </div>

    <div id="withdrawn_listings_wrapper" class="initialhidden">
        <div class="header-content">
            <div class="header-title">
                <span class="titleyour">@lang_your_withdrawn_projects@</span>
                <span class="titleuser">@lang_withdrawn_projects@ <span class="titleusername"></span></span>
            </div>
        </div>
        <div id="withdrawn_listings"></div>
    </div>

    <div id="frozen_listings_wrapper" class="initialhidden">
        <div class="header-content">
            <div class="header-title">
                <span class="titleyour">@lang_your_frozen_projects@</span>
                <span class="titleuser">@lang_frozen_projects@ <span class="titleusername"></span></span>
            </div>
        </div>
        <div id="frozen_listings"></div>
    </div>

    <div id="campaign_list_wrapper" class="initialhidden">
        <div class="header-content">
            <div class="header-title">
                <span class="titleyour">@lang_your_campaigns@</span>
                <span class="titleuser">@lang_campaigns@ <span class="titleusername"></span></span>
            </div>
        </div>
        <div id="campaign_list"></div>
        <div class="initialhidden" id="campaign_add_wrapper">
            <div class="boxpanel">
                <div class="indentedtext">
                    <div id="addcampaign">
                        <a href="#" class="inputmsg hoverlink profilelink">@lang_add_campaign@</a>
                    </div>
                </div>
            </div>
        </div>
    </div>

</div> <!-- end left column -->
'
include(profilerightbar.m4)
`
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/profile.js"></script>
<script>
(new ProfilePageClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
