package com.CPTC.CPTC_Following_Path.impl;

import java.util.Locale;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.ContributionConfiguration;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import com.ur.urcap.api.domain.data.DataModel;

public class FollowingPathProgramNodeService implements SwingProgramNodeService<FollowingPathProgramNodeContribution, FollowingPathProgramNodeView> {

	@Override
	public String getId() {
		return "CPTCFollowingPath";
	}

	@Override
	public void configureContribution(ContributionConfiguration configuration) {
		configuration.setChildrenAllowed(false);
	}

	@Override
	public String getTitle(Locale locale) {
		return "CPTC Following Path";
	}

	@Override
	public FollowingPathProgramNodeView createView(ViewAPIProvider apiProvider) {
		return new FollowingPathProgramNodeView(apiProvider);
	}

	@Override
	public FollowingPathProgramNodeContribution createNode(ProgramAPIProvider apiProvider, FollowingPathProgramNodeView view,
			DataModel model, CreationContext context) {
		return new FollowingPathProgramNodeContribution(apiProvider, view, model);
	}

}
