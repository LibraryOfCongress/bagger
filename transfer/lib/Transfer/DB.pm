package Transfer::DB;

use DBI;

require Exporter;
@ISA=qw(Exporter);
@EXPORT=qw(dbhandle);

##----------------------------------------------------------------------

my $dbh;

sub dbhandle {
	return $dbh if defined $dbh;

	my $params = {PrintError => $ENV{DBIERROR} + 0};

	$dbh = DBI->connect("dbi:Pg:dbname=transfer", "", "", $params)
				or die "FATAL: Cannot connect to transfer database. $!\n";

	return $dbh;
}

##----------------------------------------------------------------------

1;

