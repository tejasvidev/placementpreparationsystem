package placement.model;

public class Company extends User {

    private final int companyId;
    private final String industry;

    public Company(int companyId,
                   String username,
                   String passwordHash,
                   String name,
                   String email,
                   String industry) {

        super(username, passwordHash, name, email);

        if (companyId <= 0) {
            throw new IllegalArgumentException("Company ID must be positive");
        }

        if (industry == null || industry.isBlank()) {
            throw new IllegalArgumentException("Industry cannot be empty");
        }

        this.companyId = companyId;
        this.industry = industry;
    }

    public Company(
            String username,
            String passwordHash,
            String name,
            String email,
            String industry) {

        super(username, passwordHash, name, email);

        this.companyId = 0;
        this.industry = industry;
    }

    @Override
    public void displayProfile() {
        System.out.println("Company ID: " + companyId);
        System.out.println("Company Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("Industry: " + industry);
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getIndustry() {
        return industry;
    }

}