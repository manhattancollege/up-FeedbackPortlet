/*
 * Created on Feb 5, 2008
 *
 * Copyright(c) Yale University, Feb 5, 2008.  All rights reserved.
 * (See licensing and redistribution disclosures at end of this file.)
 * 
 */
package org.jasig.portlets.FeedbackPortlet.web;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlets.FeedbackPortlet.FeedbackItem;
import org.jasig.portlets.FeedbackPortlet.PortletUserPropertiesResolver;
import org.jasig.portlets.FeedbackPortlet.UserProperties;
import org.jasig.portlets.FeedbackPortlet.dao.FeedbackStore;
import org.jasig.portlets.FeedbackPortlet.service.FeedbackSubmissionListener;
import org.springframework.validation.BindException;
import org.springframework.web.portlet.mvc.SimpleFormController;

public class SubmitFeedbackFormController extends SimpleFormController {

	private static Log log = LogFactory.getLog(SubmitFeedbackFormController.class);
	private FeedbackStore feedbackStore;
	
	public SubmitFeedbackFormController() {
		setCommandName("prefs");
		setCommandClass(SubmitFeedbackForm.class);
	}

	protected void processFormSubmission(ActionRequest request,
			ActionResponse response, Object command, BindException errors) {
		
		// get the form data
		SubmitFeedbackForm form = (SubmitFeedbackForm) command;
		
		// put the form data into a new feedback object
		FeedbackItem feedback = new FeedbackItem();
		feedback.setFeedback(form.getFeedback());
		feedback.setUseragent(form.getUseragent());
		feedback.setFeedbacktype(form.getLike());
		feedback.setTabname(form.getTabname());
		
		// add information about the user to the feedback object
		if (request.getRemoteUser() != null) {
			UserProperties user = userPropertiesResolver.getProperties(request);
			feedback.setUserrole(user.getUserrole());
			if (form.getAnonymous() == null) {
				feedback.setUserid(request.getRemoteUser());
				feedback.setUsername(user.getUsername());
				feedback.setUseremail(user.getUseremail());
			}
		}
		for (FeedbackSubmissionListener listener : feedbackSubmissionListeners) {
			listener.performAction(feedback);
		}
		
		if (log.isDebugEnabled())
			log.debug("created new feedback item " + feedback.toString());
		
		feedbackStore.storeFeedback(feedback);
		
	}

	public void setFeedbackStore(FeedbackStore feedbackStore) {
		this.feedbackStore = feedbackStore;
	}

	private PortletUserPropertiesResolver userPropertiesResolver;
	public void setUserPropertiesResolver(PortletUserPropertiesResolver resolver) {
		this.userPropertiesResolver = resolver;
	}
	
	private List<FeedbackSubmissionListener> feedbackSubmissionListeners;
	public void setFeedbackSubmissionListeners(List<FeedbackSubmissionListener> listeners) {
		this.feedbackSubmissionListeners = listeners;
	}

}


/*
 * SubmitFeedbackFormController.java
 * 
 * Copyright (c) Feb 5, 2008 Yale University. All rights reserved.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE, ARE EXPRESSLY DISCLAIMED. IN NO EVENT SHALL
 * YALE UNIVERSITY OR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED, THE COSTS OF PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED IN ADVANCE OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * Redistribution and use of this software in source or binary forms, with or
 * without modification, are permitted, provided that the following conditions
 * are met.
 * 
 * 1. Any redistribution must include the above copyright notice and disclaimer
 * and this list of conditions in any related documentation and, if feasible, in
 * the redistributed software.
 * 
 * 2. Any redistribution must include the acknowledgment, "This product includes
 * software developed by Yale University," in any related documentation and, if
 * feasible, in the redistributed software.
 * 
 * 3. The names "Yale" and "Yale University" must not be used to endorse or
 * promote products derived from this software.
 */