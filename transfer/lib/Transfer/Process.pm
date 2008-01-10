package Transfer::Process;

use strict;
use warnings;
use File::Basename;
use IPC::Open3;

require Exporter;
our $VERSION=1.0;
our @ISA=qw(Exporter);
our @EXPORT=qw(run run_or_die runtime_error now);

##----------------------------------------------------------------------

sub run($;$) {
    my ($cmd, $input) = @_;

    my ($in, $out, $err);
    $err++;
    my $pid = open3($in, $out, $err, $cmd);
    
    if ($input) {
        print $in $input;
    }
    close($in);
    waitpid($pid, 0);
    
    local $/ = undef;
    my ($output) = <$out>;
    my ($errors) = <$err>;
    return ($output, $errors);
}

sub now() {
    my ($time) = run("date +%Y-%m-%dT%H:%M:%S");
    chomp($time);
    return $time;
}

sub runtime_error($$) {
    my ($cmd, $errors) = @_;
    my $self = basename($0);

    $cmd = $self unless ($cmd or $errors);

    print STDERR "$self: Error executing '$cmd'.\n" if $cmd;
    if ($errors) {
        ## Don't keep prefixing errors
        $errors = "$self: $errors" unless $errors =~ m/: /;
        print STDERR "$errors";
    }
    exit(-1);
}

sub run_or_die($;$) {
    my ($cmd, $input) = @_;
    my ($output, $errors) = run($cmd, $input);

    if ($errors) {
        runtime_error($_[0], $errors);
    }
    
    return $output;
}


##----------------------------------------------------------------------

1;
