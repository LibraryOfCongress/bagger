package Transfer::Query;

use strict;
use warnings;

require Exporter;
our @ISA=qw(Exporter);
our @EXPORT=qw(add_master add_row add_row_or_die);

use Transfer::DB;

##----------------------------------------------------------------------

sub params {
	my $table = shift;
	my (@cols, @values);
	while (@_) {
		push (@cols, shift);
		push (@values, shift);
	}

	my $fields = join(", ", @cols);
	my $params = join(", ", ("?") x @cols);

	return (dbhandle(), $table, \@cols, \@values, $fields, $params);
}

##----------------------------------------------------------------------

sub add_master {
	my ($dbh, $table, $cols, $values, $fields, $params) = params(@_);

	my $stmt = $dbh->prepare("SELECT * FROM $table WHERE $cols->[0] = ?");
	$stmt->execute($values->[0]);

	if ($stmt->rows()) {
		print STDERR "Warning: $cols->[0] '$values->[0]' already exists\n";
	} else {
		$dbh->do("INSERT INTO $table ($fields) VALUES ($params)", {}, @$values)
			or die "Cannot insert '$cols->[0]' into $table\n";
		print STDERR "Added $cols->[0] '$values->[0]' to the transport db.\n";
	}
}


##----------------------------------------------------------------------

sub add_row  {
	my ($dbh, $table, $cols, $values, $fields, $params) = params(@_);

	$dbh->do("INSERT INTO $table ($fields) VALUES ($params)", {}, @$values);
}

sub add_row_or_die {
	add_row(@_) or die "Can't add $_[1] into $_[0].\n";
}

##----------------------------------------------------------------------

1;

