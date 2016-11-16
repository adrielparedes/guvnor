/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.backend.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.structure.repositories.Comment;
import org.guvnor.structure.repositories.PortableFileDiff;
import org.guvnor.structure.repositories.PullRequest;
import org.guvnor.structure.repositories.PullRequestService;
import org.guvnor.structure.repositories.PullRequestStatus;
import org.guvnor.structure.repositories.impl.CommentImpl;
import org.guvnor.structure.repositories.impl.PullRequestImpl;
import org.jboss.errai.bus.server.annotations.Service;

import static java.lang.Integer.*;
import static java.util.stream.Collectors.*;

@Service
@ApplicationScoped
public class PullRequestServiceMock implements PullRequestService {

    private List<PullRequest> pullRequests;

    @PostConstruct
    public void initialize() {
        this.pullRequests = new ArrayList<>();
    }

    @Override
    public PullRequest createPullRequest( final String sourceRepository,
                                          final String sourceBranch,
                                          final String targetRepository,
                                          final String targetBranch,
                                          final String author,
                                          final String title ) {
        final long newId = ThreadLocalRandom.current().nextLong( 60000 );
        final PullRequestImpl pr = new PullRequestImpl( newId, sourceRepository, sourceBranch, targetRepository, targetBranch, author, title, new Date(), PullRequestStatus.OPEN );
        this.pullRequests.add( pr );
        return pr;
    }

    @Override
    public PullRequest acceptPullRequest( final PullRequest pullRequest ) {
        final PullRequest pr = changeStatus( pullRequest, PullRequestStatus.MERGED );
        return pr;
    }

    @Override
    public PullRequest rejectPullRequest( final PullRequest pullRequest ) {
        final PullRequest pr = changeStatus( pullRequest, PullRequestStatus.REJECTED );
        return pr;
    }

    @Override
    public PullRequest closePullRequest( final PullRequest pullRequest ) {
        final PullRequest pr = changeStatus( pullRequest, PullRequestStatus.CLOSED );
        return pr;
    }

    @Override
    public void addComment( final String repository,
                            final long id,
                            final String author,
                            final String content ) {

        Comment comment = new CommentImpl( UUID.randomUUID().toString(), author, Calendar.getInstance().getTime(), content );
        final PullRequest pullRequest = this.getPullRequestByRepositoryAndId( repository, id );
        final List<Comment> comments = pullRequest.getComments();
        comments.add( comment );
        final PullRequestImpl pr = new PullRequestImpl( pullRequest.getId(),
                                                        pullRequest.getSourceRepository(),
                                                        pullRequest.getSourceBranch(),
                                                        pullRequest.getTargetRepository(),
                                                        pullRequest.getTargetBranch(),
                                                        pullRequest.getAuthor(),
                                                        pullRequest.getTitle(),
                                                        pullRequest.getDate(),
                                                        pullRequest.getStatus(),
                                                        comments );
        this.pullRequests.remove( pullRequest );
        this.pullRequests.add( pr );

    }

    @Override
    public void deletePullRequest( final PullRequest pullRequest ) {
        this.pullRequests.remove( pullRequest );
    }

    public PullRequest changeStatus( PullRequest pullRequest,
                                     PullRequestStatus status ) {
        this.pullRequests.remove( pullRequest );
        final PullRequestImpl pr = new PullRequestImpl( pullRequest.getId(),
                                                        pullRequest.getSourceRepository(),
                                                        pullRequest.getSourceBranch(),
                                                        pullRequest.getTargetRepository(),
                                                        pullRequest.getTargetBranch(),
                                                        pullRequest.getAuthor(),
                                                        pullRequest.getTitle(),
                                                        pullRequest.getDate(),
                                                        status,
                                                        pullRequest.getComments() );
        this.pullRequests.add( pr );
        return pr;
    }

    @Override
    public List<PullRequest> getPullRequestsByBranch( final Integer page,
                                                      final Integer pageSize,
                                                      final String repository,
                                                      final String branch ) {
        return null;
    }

    @Override
    public List<PullRequest> getPullRequestsByRepository( final Integer page,
                                                          final Integer pageSize,
                                                          final String repository ) {
        final List<PullRequest> prs = this.pullRequests.stream().filter( ( pr ) -> pr.getTargetRepository().equals( repository ) ).collect( Collectors.toList() );
        return paginate( page, pageSize, prs );
    }

    @Override
    public List<PullRequest> getPullRequestsByStatus( final Integer page,
                                                      final Integer pageSize,
                                                      final String repository,
                                                      final PullRequestStatus status,
                                                      final boolean negated ) {
        final List<PullRequest> prs = this.pullRequests.stream().filter( ( pr ) -> negated ^ pr.getStatus().equals( status ) ).collect( Collectors.toList() );
        return paginate( page, pageSize, prs );
    }

    @Override
    public List<PortableFileDiff> diff( final PullRequest pullRequest ) {

        final ArrayList<String> lines = new ArrayList<>();
        lines.add( "+ public void main(){" );
        lines.add( "public void main(){" );
        lines.add( "- roberto" );
        lines.add( "+ juan" );
        lines.add( "}" );

        final PortableFileDiff fd = new PortableFileDiff( "file/a", "file/b", 0, 0, 0, 0, "ADD", lines );

        return Arrays.asList( fd, fd, fd, fd );
    }

    @Override
    public long numberOfPullRequestsByStatus( final String repository,
                                              final PullRequestStatus status,
                                              final boolean negated ) {
        return this.getPullRequestsByStatus( 0, 0, repository, status, negated ).size();
    }

    @Override
    public PullRequest getPullRequestByRepositoryAndId( final String repository,
                                                        final long id ) {
        final List<PullRequest> prs = this.getPullRequestsByRepository( 0, 0, repository ).stream().filter( elem -> elem.getId() == id ).collect( Collectors.toList() );
        if ( prs.size() == 0 ) {
            throw new NoSuchElementException( String.format( "The Pull Request #%s for repository <%s> does not exist", id, repository ) );
        }
        return prs.get( 0 );
    }

    @Override
    public List<Comment> getCommentsByPullRequetsId( final String repository,
                                                     final long id ) {
        return this.getPullRequestByRepositoryAndId( repository, id ).getComments();
    }

    protected List<PullRequest> paginate( Integer page,
                                          Integer pageSize,
                                          List<PullRequest> pullRequests ) {

        if ( page == 0 && pageSize == 0 ) {
            return pullRequests;
        }

        Integer finalPageSize = pageSize == 0 ? 10 : pageSize;

        final Map<Integer, List<PullRequest>> map = IntStream.iterate( 0, i -> i + finalPageSize )
                .limit( ( pullRequests.size() + finalPageSize - 1 ) / finalPageSize )
                .boxed()
                .collect( toMap( i -> i / finalPageSize,
                                 i -> pullRequests.subList( i, min( i + finalPageSize, pullRequests.size() ) ) ) );

        return new ArrayList<>( map.getOrDefault( page, new ArrayList<>() ) );

    }

}
