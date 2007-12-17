package Transport::Events;

use strict;
use warnings;

use Transfer::Process;
use Transport::Tickets;

require Exporter;
our $VERSION=1.0;
our @ISA=qw(Exporter);
our @EXPORT=qw(start_event finish_event pass_event fail_event warn_event
               run_task run_or_fail
               do_task do_task_with_warnings);

##----------------------------------------------------------------------

sub log_event($$$$$;$) {
    my ($hostname, $system, $event, $ticket, $status, $desc) = @_;
    run("transport-log-event $ticket '$event' $status $system $hostname", $desc)
}

##----------------------------------------------------------------------

sub start_event($$$$;$) {
    log_event($_[0], $_[1], $_[2], $_[3], 'started', $_[4]);
}

sub finish_event($$$$;$) {
    log_event($_[0], $_[1], $_[2], $_[3], 'completed', $_[4]);
}

sub pass_event($$$$;$) {
    log_event($_[0], $_[1], $_[2], $_[3], 'pass', $_[4]);
}

sub fail_event($$$$;$) {
    log_event($_[0], $_[1], $_[2], $_[3], 'fail', $_[4]);
}

sub warn_event($$$$;$) {
    log_event($_[0], $_[1], $_[2], $_[3], 'warning', $_[4]);
}

##----------------------------------------------------------------------

sub run_or_fail($$) {
    my ($ticket, $cmd) = @_;
    my ($output, $error) = run($cmd);
    if ($error) {
        fail_ticket($ticket);
        runtime_error($cmd, $error);
    }
    
    return $output;
}

sub run_task($$$$$) {
    my ($hostname, $system, $event, $ticket, $cmd) = @_;
    
    start_event($hostname, $system, $event, $ticket);
    my ($output, $error) = run($cmd);
    if ($error) {
        fail_event($hostname, $system, $event, $ticket);
        
        ## Let fail_ticket() figure out if this triggers a ticket failure
        fail_ticket($ticket);
        runtime_error($cmd, $error);
    }

    return $output;
}

sub do_task($$$$$) {
    my ($hostname, $system, $event, $ticket, $cmd) = @_;

    ## Run this task with events.  Capture any output as diagnostics.
    my $output = run_task($hostname, $system, $event, $ticket, $cmd);
    finish_event($hostname, $system, $event, $ticket, $output);
    pass_event($hostname, $system, $event, $ticket);
}

sub do_task_with_warnings($$$$$) {
    my ($hostname, $system, $event, $ticket, $cmd) = @_;
    
    ## Run this task with events.  Capture any output as warnings.
    my $output = run_task($hostname, $system, $event, $ticket, $cmd);
    warn_event($hostname, $system, $event, $ticket, $output)
        if $output;
    finish_event($hostname, $system, $event, $ticket);
    pass_event($hostname, $system, $event, $ticket);
}


##----------------------------------------------------------------------

1;
