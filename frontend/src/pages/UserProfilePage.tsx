import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { User, Save, Mail, Phone, MapPin } from 'lucide-react';
import { EmptyState } from '@/components/state/EmptyState';
import { AsyncPageState } from '@/components/state/AsyncPageState';
import { useUpdateUserProfile, useUserProfile } from '@/hooks/useApi';
import { useAuth } from '@/context/AuthContext';
import { getApiErrorPolicy } from '@/lib/apiError';
import { UserProfile } from '@/types';
import { useMutationFeedback } from '@/hooks/useMutationFeedback';

const UserProfilePage = () => {
    const [user, setUser] = useState<UserProfile>({
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        street: '',
        number: '',
        postalCode: '',
        city: '',
        country: '',
    });
    const [isSaving, setIsSaving] = useState(false);
    const { user: authUser } = useAuth();
    const customerId = authUser?.userId;
    const {
        data: profileData,
        isLoading,
        isError,
        error,
        refetch,
    } = useUserProfile(customerId);
    const updateUserProfileMutation = useUpdateUserProfile();
    const runWithFeedback = useMutationFeedback();

    useEffect(() => {
        if (profileData) {
            setUser(profileData);
        }
    }, [profileData]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    const saveChanges = async () => {
        if (!customerId) return;
        setIsSaving(true);
        await runWithFeedback(
            () => updateUserProfileMutation.mutateAsync({ id: customerId, data: user }),
            {
                context: 'userProfile.update',
                successMessage: 'Profile updated successfully',
            },
        );
        setIsSaving(false);
    };

    return (
        <AsyncPageState
            isLoading={isLoading}
            isError={isError}
            errorMessage={getApiErrorPolicy(error).message}
            onRetry={() => { refetch(); }}
            loadingMessage="Loading profile..."
            loadingClassName="min-h-[50vh]"
            empty={!customerId}
            emptyState={
                <EmptyState
                    title="User profile unavailable"
                    description="Sign in again to load your profile data."
                    icon={<User className="w-16 h-16 text-muted-foreground mx-auto mb-4" />}
                />
            }
        >
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

                            <div className="pt-2 border-t border-border">
                                <h3 className="text-sm font-semibold text-foreground flex items-center gap-2 mb-4">
                                    <MapPin className="w-4 h-4" />
                                    Address
                                </h3>
                                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                                    <div className="sm:col-span-2 space-y-2">
                                        <label className="text-sm font-medium text-foreground">Street</label>
                                        <Input
                                            type="text"
                                            name="street"
                                            value={user.street}
                                            onChange={handleInputChange}
                                            placeholder="Main Street"
                                        />
                                    </div>
                                    <div className="space-y-2">
                                        <label className="text-sm font-medium text-foreground">Number</label>
                                        <Input
                                            type="text"
                                            name="number"
                                            value={user.number}
                                            onChange={handleInputChange}
                                            placeholder="42A"
                                        />
                                    </div>
                                </div>
                                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mt-4">
                                    <div className="space-y-2">
                                        <label className="text-sm font-medium text-foreground">Postal Code</label>
                                        <Input
                                            type="text"
                                            name="postalCode"
                                            value={user.postalCode}
                                            onChange={handleInputChange}
                                            placeholder="10001"
                                        />
                                    </div>
                                    <div className="space-y-2">
                                        <label className="text-sm font-medium text-foreground">City</label>
                                        <Input
                                            type="text"
                                            name="city"
                                            value={user.city}
                                            onChange={handleInputChange}
                                            placeholder="Amsterdam"
                                        />
                                    </div>
                                    <div className="space-y-2">
                                        <label className="text-sm font-medium text-foreground">Country</label>
                                        <Input
                                            type="text"
                                            name="country"
                                            value={user.country}
                                            onChange={handleInputChange}
                                            placeholder="Netherlands"
                                        />
                                    </div>
                                </div>
                            </div>

                            <div className="pt-4">
                                <Button onClick={saveChanges} disabled={isSaving || !customerId} size="lg">
                                    <Save className="w-4 h-4 mr-2" />
                                    {isSaving ? 'Saving...' : 'Save Changes'}
                                </Button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            </div>
        </AsyncPageState>
    );
};

export default UserProfilePage;
