function NotificationClass() {}
pl.implement(NotificationClass, {
    store: function(json) {
        var self = this;
        for (k in json) {
            self[k] = json[k];
        }
        self.message = self.title ? SafeStringClass.prototype.htmlEntities(self.title) : '';
        self.messageclass = self.read ? '' : ' inputmsg'; // unread
        if (!self.notify_type) {
            self.type = 'notification';
            self.linktext = '@lang_notification_page@';
        }
        else if (self.notify_type.match('comment')) {
            self.type = 'comment';
            self.linktext = '@lang_notification_comment_page@';
        }
        else if (self.notify_type.match('bid')) {
            self.type = 'bid';
            self.linktext = '@lang_notification_investment_page@';
        }
        else if (self.notify_type.match('ask_listing_owner')) {
            self.type = 'ask_listing_owner';
            self.linktext = '@lang_notification_qa_page@';
        }
        else if (self.notify_type.match('private_message')) {
            self.type = 'private_message';
            self.linktext = '@lang_notification_private_msg_page@';
        }
        else if (self.notify_type.match(/.*listing.*/)) {
            self.type = 'notification';
            self.linktext = '@lang_notification_listing_page@';
        }
        else {
            self.type = 'notification';
            self.linktext = '@lang_notification_page@';
        }
        self.date = self.create_date || self.sent_date;
        self.datetext = self.date ? DateClass.prototype.format(self.date) : '';
        self.openanchor = self.link ? '<a href="' + self.link + '" class="hoverlink notifylink' + self.messageclass + '">' : '';
        self.closeanchor = self.link ? '</a>' : '';
    },
    display: function(json) {
        var self = this,
            banner = new CompanyBannerClass();
        if (json.notification) {
            this.store(json.notification);
        }
        else if (json) {
            this.store(json);
        }
        else {
            this.setEmpty();
        }
        pl('#notificationmessage').text(this.message);
        if (this.link) {
            pl('#notificationlink').attr({href: this.link}).text(this.linktext);
            pl('#notificationview').show();
        }
        if (!json.listing && json.listing_id) { // construct virtual listing
            json.listing = {
                listing_id: json.listing_id,
                title: json.listing_name,
                owner: json.listing_owner,
                category: json.listing_category,
                brief_address: json.listing_brief_address,
                mantra: json.listing_mantra,
                logo: json.listing_logo_url,
                status: 'active'
            };
        }
        if (json.listing) {
            banner.displayMinimal(json);
            pl('#notificationlistingwrapper .companyheader').addClass('notificationlistingheader');
            pl('#notificationlistingwrapper .companybanner').addClass('notificationlistingbanner');
            pl('#notificationlistingwrapper').addClass('hoverlink').bind('click', function() {
                document.location = self.link;
            });
            pl('#notificationlistingwrapper').show();
        }
    },
    setEmpty: function() {
        var self = this,
            emptyJson = {
                notify_type: 'notification',
                title: '@lang_no_notifications@',
                text_1: null,
                create_date: null,
                sent_date: null,
                read: true,
                link: null
            };
        self.store(emptyJson);
    },
    makeHtml: function() {
        var self = this;
        return '\
        <div class="notifyline">\
            <span class="sideboxicon" style="overflow:visible;">\
                <div class="'+self.type+'icon" style="overflow:visible;"></div>\
            </span>\
            <span class="notifytext">\
                '+self.openanchor+'\
                '+self.message+'\
                '+self.closeanchor+'\
            </span>\
            <span class="notifydate">'+self.datetext+'</span>\
        </div>\
        ';
    }
});

function NotifyListClass() {}
pl.implement(NotifyListClass, {
    store: function(json) {
        var self = this,
            jsonlist = json && json.notifications ? json.notifications : [],
            notification,
            i;
        self.notifications = [];
        if (jsonlist.length) {
            for (i = 0; i < jsonlist.length; i++) {
                notification = new NotificationClass();
                notification.store(jsonlist[i]);
                self.notifications.push(notification);
            }
        }
        else {
            notification = new NotificationClass();
            notification.setEmpty();
            self.notifications.push(notification);
        }
        self.more_results_url = self.notifications.length > 0 && json.notifications_props && json.notifications_props.more_results_url;
    },
    bindMoreResults: function() {
        var self = this;
        pl('#moreresults').bind({
            click: function() {
                var completeFunc = function(json) {
	                    var self = this,
	                    html = '',
	                    i,
	                    jsonlist = json && json.notifications ? json.notifications : [],
	                    notification;
		                self.notifications = [];
		                if (jsonlist.length) {
		                    for (i = 0; i < jsonlist.length; i++) {
		                        notification = new NotificationClass();
		                        notification.store(jsonlist[i]);
		                        self.notifications.push(notification);
		                    }
		                }
		                else {
		                    notification = new NotificationClass();
		                    notification.setEmpty();
		                    self.notifications.push(notification);
		                }
		                for (i = 0; i < self.notifications.length; i++) {
		                    notification = self.notifications[i];
		                    html += notification.makeHtml();
		                }
		                if (!self.notifications.length) {
		                    notification = new NotificationClass();
		                    notification.setEmpty();
		                    html += notification.makeHtml();
		                }
		                self.more_results_url = self.notifications.length > 0 && json.notifications_props && json.notifications_props.more_results_url;
                        
	                    if (html) {
                            pl('#moreresults').before(html);
                        }
                        if (self.more_results_url) {
                            pl('#moreresultsurl').text(self.more_results_url);
                            pl('#moreresultsmsg').text('@lang_more@');
                        }
                        else {
                            pl('#moreresultsmsg').text('');
                            pl('#moreresults').removeClass('hoverlink').unbind();
                        }
                    },
                    more_results_url = pl('#moreresultsurl').text(),
                    ajax,
                    data,
                    i;
                if (more_results_url) {
                    ajax = new AjaxClass(more_results_url, 'moreresultsmsg', completeFunc);
                    ajax.setGetData(data);
                    ajax.call();
                }
                else {
                    pl('#moreresultsmsg').text('');
                    pl('#moreresults').removeClass('hoverlink').unbind();
                }
            }
        });
    },
    display: function(json) {
        var self = this,
            html = '',
            i,
            notification;
        if (json !== undefined) {
            self.store(json);
        }
        for (i = 0; i < self.notifications.length; i++) {
            notification = self.notifications[i];
            html += notification.makeHtml();
        }
        if (!self.notifications.length) {
            notification = new NotificationClass();
            notification.setEmpty();
            html += notification.makeHtml();
        }
        if (self.more_results_url) {
        	html += '<div class="showmore hoverlink" id="moreresults"><span class="initialhidden" id="moreresultsurl">' + self.more_results_url + '</span><span id="moreresultsmsg">More...</span></div>\n';
        }
        pl('#notifylist').html(html);
        if (self.more_results_url) {
            this.bindMoreResults();
        }
    }
});

function NotificationPageClass() {
    this.json = {};
};
pl.implement(NotificationPageClass,{
    loadPage: function() {
        var successFunc = function(json) {
                var header = new HeaderClass(),
                    notifyList = new NotifyListClass();
                header.setLogin(json);
                if (!json || !json.loggedin_profile) { // must be logged in for this page
                    header.showLoginPopup();
                }
                else {
                    notifyList.display(json);
                }
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            errorFunc = function(statusCode, json) {
                var header = new HeaderClass();
                header.setLogin(json);
                header.showLoginPopup();
                pl('.preloader').hide();
                pl('.wrapper').show();
            };
        ajax = new AjaxClass('/notification/user', 'notificationsmsg', null, successFunc, null, errorFunc);
        ajax.setGetData({ max_results: 10 });
        ajax.call();
    }
});

function SingleNotificationPageClass() {
    var queryString = new QueryStringClass();
    this.id = queryString.vars.id;
    this.notification_id = this.id;
    this.json = {};
};
pl.implement(SingleNotificationPageClass,{
    loadPage: function() {
        var notif_id = this.notification_id,
            completeFunc = function(json) {
                var header = new HeaderClass(),
                    notification = new NotificationClass();
                header.setLogin(json);
                notification.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
             },
            ajax = new AjaxClass('/notification/get/' + notif_id, 'notificationmsg', completeFunc);
        if (notif_id) {
            ajax.call();
        }
        else {
            window.location = '/';
        }
    }
});

