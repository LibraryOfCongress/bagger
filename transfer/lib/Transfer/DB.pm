package Transfer::DB;

use DBI;

use File::Basename;
use Cwd qw(abs_path);

require Exporter;
@ISA=qw(Exporter);
@EXPORT=qw(dbhandle);

##----------------------------------------------------------------------

my $dbh;

sub get_connection_params() {
    my $conf = abs_path(dirname($0) . "/../etc/transfer.conf");
    my %config;
    
    if (-e $conf) {
        open(my $fh, $conf);
        local $/ = undef;
        my $input = <$fh>;
        %config = $input =~ m/^[ \t]*([^\s=]+)[ \t]*=[ \t]*(.*?)[ \t]*$/mg;
        close($fh);
    }

    ## Use reasonable defaults
    $config{dbname}   ||= "transfer";
    $config{username} ||= ""        ;
    $config{password} ||= ""        ;
    
    my $connect = "dbi:Pg:dbname=$config{dbname}";
    return ($connect, $config{username}, $config{password});
}

sub dbhandle {
	return $dbh if defined $dbh;

    my ($connect, $user, $pass) = get_connection_params();
	my $params = {PrintError => $ENV{DBIERROR} + 0};

	$dbh = DBI->connect($connect, $user, $pass, $params)
		or die "FATAL: Cannot connect to transfer database. $!\n";

	return $dbh;
}

##----------------------------------------------------------------------

1;

