`
    <div class="initialhidden" id="askingpricewrapper">
        <div class="header-content header-boxpanel-initial header-boxpanel-full">@lang_asking@</div>
        <div class="sidebox uneditabletext askingbox-full">
                <span class="sideboxtitlecompact sideboxnum stan-osoby" id="askingamt"></span>
                <span class="sideboxdesc stan-projektu"> @lang_for_equity@
                    <span class="sideboxnum" id="askingpct"></span><span class="sideboxnum">%</span> @lang_equity@ </span>
                <span class="sideboxdesc stan-projektu">@lang_valuation_is@ <span class="sideboxnum" id="askingval"></span></span>
        </div>
    </div>

    <div class="initialhidden" id="orderbookwrapper">
        <div class="header-content header-boxpanel-initial header-boxpanel-full clear">@lang_latest_bids@
            <span class="newlistingtitlemsg" id="orderbooktitlemsg"></span>
        </div>

        <div class="sidebox orderbook lastorder">
            <div>@lang_last_bid@</div>
            <div class="lastorderamt sideboxnum" id="last_investor_bids_amt"></div>
            <div id="last_investor_bids_details"></div>
            <div class="lastdate" id="last_investor_bids_date"></div>
        </div>

        <div class="sidebox orderbook lastorder">
            <div>@lang_last_ask@</div>
            <div class="lastorderamt sideboxnum" id="last_owner_bids_amt"></div>
            <div id="last_owner_bids_details"></div>
            <div class="lastdate" id="last_owner_bids_date"></div>
        </div>

        <div class="sidebox orderbooklast lastorder">
            <div>@lang_last_sale@</div>
            <div class="lastorderamt sideboxnum" id="last_accepted_bids_amt"></div>
            <div id="last_accepted_bids_details"></div>
            <div class="lastdate" id="last_accepted_bids_date"></div>
        </div>

        <div class="boxtitlegap smokegrey clear">@lang_order_book@</div>

        <div class="sidebox orderbook" id="orderbook_investor_bids"></div>
        <div class="sidebox orderbook" id="orderbook_owner_bids"></div>
        <div class="sidebox orderbooklast" id="orderbook_accepted_bids"></div>

    </div>
'
