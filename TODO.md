Global App Changes
==================

1. Use a database to centralize information about volunteer hours. This will allow
for multiple instances of the application to be running so volunteers can track their
time in multiple places (i.e. sign-in at the front door and sign-out at the back).

Databases to integrate:
* [SQLite](https://www.sqlite.org/)
* [PostgreSQL](http://www.postgresql.com)

2. Also, volunteers should never be deleted from the system. They should only be
disabled or not shown. This is for auditing purposes. The only way to delete a
volunteer would be directly through the database and not through the application
itself.

Individual sections of the Software
===================================

Add Volunteer Screen
--------------------

1. Track birthdate and not the volunteer's age.

2. ID should default to email address and allow for a custom ID. This way if a
volunteer does not have an email address, they can still have a valid record in
the system.

3. Add the following fields:
	* City
        * State
        * Zip

4. Besides the ID for each volunteer, used to gain access to their status page,
volunteers will need a password. This will prevent abuse and inadvertant clock-in/out.

5. The order of the fields should be based on the volunteer application form.
The order is as follows:
	* Name
	* DoB
	* Address
	* City
	* State
	* Zip
	* Phone
	* Email
	* Login ID (defaults to email, can be custom)
	* Password/phrase/code (at least 8 characters, defaults to a random set)

6. The required fields are:
	* Name
	* DoB
	* Login ID
	* Password
All other fields are optional.

7. If the volunteer is under 18 years of age, we need one additional field for
the partent/guardian name.

Volunteer Account Page
----------------------

1. Do not show See Information. This screen can be accessed by anyone who knows
another volunteer's Login ID. Not all volunteers would be happy to have other
volunteers see their phone number or mailing address. This information should be
considered confidential and only be available to administrators of the
application.

2. Sign In/Out should require the volunteer to enter their password to complete
the action.

3. Add Hours for Today alongside the Total hours.

Administration Page
-------------------
1. As mentioned above, do not delete, only disable volunteers. Disabling a
volunteer would simply make them no longer show up in the Administrator screen.

2. Need a method to edit the history of any volunteer. For instance, if a
volunteer arrives at noon and then forgets to sign-in until 2, an administrator
should be able to adjust the sign-in time to noon. Likewise for signing out.
