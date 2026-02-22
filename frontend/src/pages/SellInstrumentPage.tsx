import { FormEvent, useMemo, useState } from 'react';
import { CheckCircle2, DollarSign } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useAuth } from '@/context/AuthContext';
import { useUserProfile } from '@/hooks/useApi';

interface SellFormState {
  brandName: string;
  modelName: string;
  instrumentType: string;
  desiredPrice: string;
  conditionDetails: string;
  contactName: string;
  contactEmail: string;
  contactPhone: string;
}

const conditionMessages = [
  {
    label: 'Excellent',
    detail: 'Pristine setup, recent servicing, and negligible wear.',
    priceImpact: 'Pricing aligns with top-market comps.',
  },
  {
    label: 'Good',
    detail: 'Fully functional with visible wear but no major defects.',
    priceImpact: 'Expect standard market price minus minor wear allowance.',
  },
  {
    label: 'Fair',
    detail: 'Play-ready but may need setup or minor repair.',
    priceImpact: 'We price below average to offset light refurbishment.',
  },
];

const SellInstrumentPage = () => {
  const { user: authUser } = useAuth();
  const { data: profile } = useUserProfile(authUser?.userId);
  const [submitted, setSubmitted] = useState(false);
  const [form, setForm] = useState<SellFormState>({
    brandName: '',
    modelName: '',
    instrumentType: '',
    desiredPrice: '',
    conditionDetails: '',
    contactName: '',
    contactEmail: '',
    contactPhone: '',
  });

  const resolvedContact = useMemo(() => ({
    contactName: form.contactName || `${profile?.firstName ?? authUser?.firstName ?? ''} ${profile?.lastName ?? authUser?.lastName ?? ''}`.trim(),
    contactEmail: form.contactEmail || profile?.email || authUser?.email || '',
    contactPhone: form.contactPhone || profile?.phoneNumber || '',
  }), [form, profile, authUser]);

  const onSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSubmitted(true);
  };

  if (submitted) {
    return (
      <div className="min-h-screen bg-background">
        <div className="bg-[#073642] border-b-2 border-[#002b36]">
          <div className="container mx-auto px-4 py-6">
            <div className="flex items-center gap-3">
              <CheckCircle2 className="w-8 h-8 text-[#859900]" />
              <h1 className="font-[family-name:var(--font-display)] text-4xl text-[#fdf6e3] tracking-tight">
                REQUEST SENT
              </h1>
            </div>
          </div>
        </div>
        <div className="container mx-auto px-4 py-8">
          <div className="max-w-2xl mx-auto bg-card rounded-xl border border-border p-8">
            <p className="text-lg font-semibold text-foreground mb-2">
              Thanks for submitting your instrument details.
            </p>
            <p className="text-muted-foreground">
              You will be contacted shortly.
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="bg-[#073642] border-b-2 border-[#002b36]">
        <div className="container mx-auto px-4 py-6">
        <div className="flex items-center gap-3">
            <DollarSign className="w-8 h-8 text-[#b58900]" />
            <h1 className="font-[family-name:var(--font-display)] text-4xl text-[#fdf6e3] tracking-tight">
              SELL YOUR INSTRUMENT
            </h1>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <form onSubmit={onSubmit} className="max-w-3xl mx-auto bg-card rounded-xl border border-border p-6 space-y-6">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-2">
              <label className="text-sm font-medium text-foreground">Brand Name</label>
              <Input
                value={form.brandName}
                onChange={(e) => setForm((prev) => ({ ...prev, brandName: e.target.value }))}
                placeholder="Fender, Yamaha, Gibson..."
              />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium text-foreground">Model Name</label>
              <Input
                value={form.modelName}
                onChange={(e) => setForm((prev) => ({ ...prev, modelName: e.target.value }))}
                placeholder="Model / Series"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-2">
              <label className="text-sm font-medium text-foreground">Instrument Type</label>
              <Input
                value={form.instrumentType}
                onChange={(e) => setForm((prev) => ({ ...prev, instrumentType: e.target.value }))}
                placeholder="Electric Guitar, Drum Kit, Synth..."
              />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium text-foreground">Desired Price (EUR)</label>
              <Input
                type="number"
                min="1"
                step="0.01"
                value={form.desiredPrice}
                onChange={(e) => setForm((prev) => ({ ...prev, desiredPrice: e.target.value }))}
                placeholder="500"
              />
            </div>
          </div>

          <div className="space-y-2">
            <p className="text-sm font-semibold text-foreground uppercase tracking-wide">Condition Details</p>
            <div className="grid gap-3">
              {conditionMessages.map((condition) => (
                <div
                  key={condition.label}
                  className="rounded-xl border border-[#93a1a1] bg-[#fdf6e3] px-3 py-3 text-sm text-[#586e75]"
                >
                  <p className="font-semibold text-[#073642]">{condition.label}</p>
                  <p>{condition.detail}</p>
                  <p className="text-xs text-[#2aa198]">{condition.priceImpact}</p>
                </div>
              ))}
            </div>
            <div className="rounded-md border border-[#93a1a1] bg-[#fdf6e3] px-3 py-2 text-xs text-[#586e75]">
              <p>Share wear, setup, and extras in the box below.</p>
              <p className="font-semibold text-[#073642]">Poor-condition instruments are not accepted.</p>
            </div>
            <textarea
              value={form.conditionDetails}
              onChange={(e) => setForm((prev) => ({ ...prev, conditionDetails: e.target.value }))}
              placeholder="Describe the instrument’s current condition, wear, and any extras/accessories."
              className="w-full min-h-[120px] rounded-md border border-[#93a1a1] bg-[#fdf6e3] px-3 py-2 text-sm text-[#073642] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#268bd2]"
            />
          </div>

          <div className="pt-2 border-t border-border space-y-4">
            <h2 className="text-lg font-semibold text-foreground">Contact Details</h2>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <Input
                value={resolvedContact.contactName}
                onChange={(e) => setForm((prev) => ({ ...prev, contactName: e.target.value }))}
                placeholder="Full Name"
              />
              <Input
                type="email"
                value={resolvedContact.contactEmail}
                onChange={(e) => setForm((prev) => ({ ...prev, contactEmail: e.target.value }))}
                placeholder="Email"
              />
              <Input
                value={resolvedContact.contactPhone}
                onChange={(e) => setForm((prev) => ({ ...prev, contactPhone: e.target.value }))}
                placeholder="Phone"
              />
            </div>
          </div>

          <Button
            type="submit"
            size="lg"
            className="w-full"
            disabled={
              !form.brandName.trim()
              || !form.modelName.trim()
              || !form.instrumentType.trim()
              || !form.desiredPrice.trim()
              || Number(form.desiredPrice) <= 0
            }
          >
            Submit Instrument Details
          </Button>
        </form>
      </div>
    </div>
  );
};

export default SellInstrumentPage;
