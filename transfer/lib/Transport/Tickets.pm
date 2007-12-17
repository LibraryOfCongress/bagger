package Transport::Tickets;

use strict;
use warnings;
use Transfer::DB;
use Transfer::Process;
use Transfer::Query;

require Exporter;
our @ISA=qw(Exporter);
our @EXPORT=qw(queued_tickets 
               start_ticket finish_ticket fail_ticket queue_ticket
               track_ticket_failures
               get_ticket_queue
               update_ticket_size);

##----------------------------------------------------------------------

sub queued_tickets () {
    my @results = ();
    
    my $tickets = select_rows(<<"    EOSQL");
        SELECT ticket_id, package_name, repository, estimated_size
        FROM transport.tickets
        WHERE status = 'queued'
        ORDER BY created_timestamp;
    EOSQL
    
    my $ticket_info = <<"    EOSQL";
        SELECT role, file_owner, hostname, root_directory
        FROM transport.locations
        WHERE ticket_id = ?
        ORDER BY location_id;
    EOSQL
    
    foreach (@$tickets) {
        my ($id, $package, $repo, $size) = @$_;
        my $locations = select_rows($ticket_info, $id);
        
        my $info = {
            ticket     => $id,
            package    => $package, 
            repository => $repo,
            size       => $size,
            locations  => []
        };

        foreach (@$locations) {
            my ($role, $owner, $hostname, $dir) = @$_;            
            push @{$info->{locations}}, {
                role     => $role, 
                owner    => $owner,
                hostname => $hostname, 
                dir      => $dir
            };
        }
        push(@results, $info);
    }
    
    return @results;
}


##----------------------------------------------------------------------

sub update_ticket_size ($$) {
    my ($ticket, $size) = @_;
    my $dbh = dbhandle();
    
    my $stmt = $dbh->prepare(<<"    EOSQL");
        UPDATE transport.tickets SET estimated_size=? WHERE ticket_id=?
    EOSQL
    $stmt->execute($size, $ticket);
}

##----------------------------------------------------------------------

sub run_update ($$) {
    my ($ticket, $status) = @_;
    run_or_die("transport-update-package $ticket $status");
}

## Valid statuses are:
## registered, queued, started, paused, failed, completed 
## Registered is default status, where every ticket starts.

sub queue_ticket($) {
    run_update(shift, "queued");
}

sub start_ticket($) {
    run_update(shift, "started");
}

sub pause_ticket($) {
    run_update(shift, "paused");
}

sub finish_ticket($) {
    run_update(shift, "completed");
}

my $capture_failures;

sub track_ticket_failures($) {
    $capture_failures = shift;
}

sub fail_ticket($) {
    run_update(shift, "failed") if $capture_failures;
}

##----------------------------------------------------------------------

sub parse_path($) {
    my $path = shift;
    return ("", "", "") unless $path;
    return ($path =~ m/^(?:(.*?)@)?(?:(.*?):)?(.*)$/);
}

sub get_ticket_queue {
    my $cmd = "transport-browse-queue";

    my ($output, $error) = run($cmd);
    runtime_failure($cmd) if $error;
    
    my @tickets;
    
    foreach (split("\n\n", $output)) {
        my %ticket;
        
        ($ticket{id})        = m/^Ticket:\s+(\d+)$/m;
        ($ticket{package})   = m/^Package:\s+(.*)$/m;
        ($ticket{repo})      = m/^Repository:\s+(.*)$/m;
        ($ticket{size})      = m/^Estimated-Size:\s+(.*)$/m;
        ($ticket{source})    = m/^Source:\s+(.*)$/m;
        ($ticket{workspace}) = m/^Workspace:\s+(.*)$/m;
        ($ticket{dest})      = m/^Destination:\s+(.*)$/m;

        ($ticket{source_user}, $ticket{source_host}, $ticket{source_dir}) 
            = parse_path($ticket{source});
        ($ticket{dest_user}, $ticket{dest_host}, $ticket{dest_dir}) 
            = parse_path($ticket{dest});
        ($ticket{work_user}, $ticket{work_host}, $ticket{work_dir}) 
            = parse_path($ticket{workspace});

        push(@tickets, \%ticket);
    }
    
    return @tickets;
}

##----------------------------------------------------------------------


1;

