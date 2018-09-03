package com.cloudzone.gitlabserver.service.impl;


import com.cloudzone.GitLabServiceAPI;
import com.cloudzone.common.entity.gitlab.*;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gitlab Service Impl
 *
 * @author tongqiangying@gmail.com
 * @since 2018/3/9
 */
@Service
public class GitlabServiceImpl implements GitLabServiceAPI {

    @Value("${gitlab.server.hostUrl}")
    private String gitlabHostUrl;

    @Override
    public GitlabSessionVo login(String username, String password) throws IOException {
        GitlabSession session = GitlabAPI.connect(gitlabHostUrl, username, password);
        GitlabUserVo gitlabUserVo = new GitlabUserVo(session.getId(), session.getUsername(), session.getEmail(), session.getName(), session.getSkype(), session.getProvider(), session.getState(), session.getPrivateToken(), session.getWebsiteUrl(), session.getColorSchemeId(), session.getExternProviderName(), session.getCreatedAt());
        return new GitlabSessionVo(session.getPrivateToken(), gitlabUserVo);
    }

    @Override
    public List<GitlabGroupVo> getGroupsForUser(String privateToken) throws IOException {
        GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabHostUrl, privateToken);
        List<GitlabGroup> gitlabGroupList = gitlabAPI.getGroups();
        List<GitlabGroupVo> list = new ArrayList<GitlabGroupVo>(gitlabGroupList.size());
        for (GitlabGroup group : gitlabGroupList) {
            list.add(new GitlabGroupVo(group.getId(), group.getName(), group.getPath(), group.getWebUrl()));
        }
        return list;
    }

    @Override
    public List<GitlabProjectVo> getProjectsByGroupId(String privateToken, int groupId) throws IOException {
        GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabHostUrl, privateToken);
        List<GitlabProject> gitlabProjects = gitlabAPI.getGroupProjects(groupId);
        List<GitlabProjectVo> list = new ArrayList<GitlabProjectVo>(gitlabProjects.size());
        for (GitlabProject project : gitlabProjects) {
            list.add(new GitlabProjectVo(project.getId(), project.getName(), project.getDescription(), project.getDefaultBranch(), project.getPath(), project.getPathWithNamespace(), project.getSshUrl(), project.getWebUrl(), project.getHttpUrl(), project.getLastActivityAt()));
        }
        return list;
    }

    @Override
    public List<GitlabBranchVo> getBranchesByProjectId(String privateToken, int projectId) throws IOException {
        GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabHostUrl, privateToken);
        List<GitlabBranch> gitlabBranches = gitlabAPI.getBranches(projectId);
        List<GitlabBranchVo> list = new ArrayList<GitlabBranchVo>(gitlabBranches.size());
        for (GitlabBranch branch : gitlabBranches) {
            GitlabUser committerOld = branch.getCommit().getCommitter();
            GitlabUserVo committer = null;
            if (committerOld != null) {
                committer = new GitlabUserVo(committerOld.getId(), committerOld.getUsername(), committerOld.getEmail(), committerOld.getName(), committerOld.getSkype(), committerOld.getProvider(), committerOld.getState(), committerOld.getPrivateToken(), committerOld.getWebsiteUrl(), committerOld.getColorSchemeId(), committerOld.getExternProviderName(), committerOld.getCreatedAt());
            }
            GitlabBranchCommitVo commitVo = new GitlabBranchCommitVo(branch.getCommit().getId(), branch.getCommit().getTree(), branch.getCommit().getMessage(), branch.getCommit().getCommittedDate(), committer);
            list.add(new GitlabBranchVo(branch.getName(), commitVo));
        }
        return list;
    }

    @Override
    public List<GitlabTagVo> getTagsByProjectId(String privateToken, int projectId) throws IOException {
        GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabHostUrl, privateToken);
        List<GitlabTag> gitlabTags = gitlabAPI.getTags(projectId);
        List<GitlabTagVo> list = new ArrayList<GitlabTagVo>(gitlabTags.size());
        for (GitlabTag tag : gitlabTags) {
            GitlabUser committerOld = tag.getCommit().getCommitter();
            GitlabUserVo committer = null;
            if (committerOld != null) {
                committer = new GitlabUserVo(committerOld.getId(), committerOld.getUsername(), committerOld.getEmail(), committerOld.getName(), committerOld.getSkype(), committerOld.getProvider(), committerOld.getState(), committerOld.getPrivateToken(), committerOld.getWebsiteUrl(), committerOld.getColorSchemeId(), committerOld.getExternProviderName(), committerOld.getCreatedAt());
            }

            GitlabBranchCommitVo commitVo = new GitlabBranchCommitVo(tag.getCommit().getId(), tag.getCommit().getTree(), tag.getCommit().getMessage(), tag.getCommit().getCommittedDate(), committer);
            GitlabReleaseVo releaseVo = new GitlabReleaseVo(tag.getRelease().getTagName(), tag.getRelease().getDescription());
            list.add(new GitlabTagVo(tag.getName(), tag.getMessage(), commitVo, releaseVo));
        }
        return list;
    }
}
