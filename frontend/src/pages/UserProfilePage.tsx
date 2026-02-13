import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { User, Save, Mail, Phone } from 'lucide-react';
import { getStoredUser } from '@/api/client';

interface UserData {
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
}

const UserProfilePage = () => {
    const [user, setUser] = useState<UserData>({ firstName: '', lastName: '', email: '', phoneNumber: '' });
    const [isSaving, setIsSaving] = useState(false);
    const storedUser = getStoredUser();
    const customerId = storedUser?.userId;

    useEffect(() => {
        fetch(`/api/users/${customerId}`, {
            credentials: 'include'
        })
            .then(response => response.json())
            .then(data => setUser(data))
            .catch(error => console.error('Error fetching user data:', error));
    }, [customerId]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    const saveChanges = () => {
        setIsSaving(true);
        fetch(`/api/users/${customerId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(user)
        })
            .then(response => {
                if (response.ok) {
                    alert('Profile updated successfully!');
                } else {
                    console.error('Failed to update user data');
                    alert('Failed to update profile.');
                }
            })
            .catch(error => console.error('Error updating user data:', error))
            .finally(() => setIsSaving(false));
    };

    return (
        <div className="min-h-screen bg-background">
            {/* Header Section */}
            <div className="bg-[#073642] border-b-2 border-[#002b36]">
                <div className="container mx-auto px-4 py-6">
                    <div className="flex items-center gap-3">
                        <User className="w-8 h-8 text-[#268bd2]" />
                        <h1 className="font-[family-name:var(--font-display)] text-4xl text-[#fdf6e3] tracking-tight">
                            EDIT PROFILE
                        </h1>
                    </div>
                </div>
            </div>

            <div className="container mx-auto px-4 py-8">
                <div className="max-w-2xl mx-auto">

                    <div className="bg-card rounded-xl border border-border p-6">
                        {/* Avatar Section */}
                        <div className="flex items-center gap-4 mb-8 pb-6 border-b border-border">
                            <div className="w-20 h-20 rounded-full bg-gradient-to-br from-primary to-primary/50 flex items-center justify-center">
                                <span className="text-2xl font-bold text-primary-foreground">
                                    {user.firstName?.[0]}{user.lastName?.[0]}
                                </span>
                            </div>
                            <div>
                                <h2 className="text-xl font-semibold text-foreground">
                                    {user.firstName} {user.lastName}
                                </h2>
                                <p className="text-sm text-muted-foreground">{user.email}</p>
                            </div>
                        </div>

                        {/* Form */}
                        <div className="space-y-6">
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <div className="space-y-2">
                                    <label className="text-sm font-medium text-foreground">First Name</label>
                                    <Input
                                        type="text"
                                        name="firstName"
                                        value={user.firstName}
                                        onChange={handleInputChange}
                                        placeholder="First Name"
                                    />
                                </div>
                                <div className="space-y-2">
                                    <label className="text-sm font-medium text-foreground">Last Name</label>
                                    <Input
                                        type="text"
                                        name="lastName"
                                        value={user.lastName}
                                        onChange={handleInputChange}
                                        placeholder="Last Name"
                                    />
                                </div>
                            </div>

                            <div className="space-y-2">
                                <label className="text-sm font-medium text-foreground flex items-center gap-2">
                                    <Mail className="w-4 h-4" />
                                    Email Address
                                </label>
                                <Input
                                    type="email"
                                    name="email"
                                    value={user.email}
                                    onChange={handleInputChange}
                                    placeholder="Email"
                                />
                            </div>

                            <div className="space-y-2">
                                <label className="text-sm font-medium text-foreground flex items-center gap-2">
                                    <Phone className="w-4 h-4" />
                                    Phone Number
                                </label>
                                <Input
                                    type="text"
                                    name="phoneNumber"
                                    value={user.phoneNumber}
                                    onChange={handleInputChange}
                                    placeholder="Phone Number"
                                />
                            </div>

                            <div className="pt-4">
                                <Button onClick={saveChanges} disabled={isSaving} size="lg">
                                    <Save className="w-4 h-4 mr-2" />
                                    {isSaving ? 'Saving...' : 'Save Changes'}
                                </Button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default UserProfilePage;
